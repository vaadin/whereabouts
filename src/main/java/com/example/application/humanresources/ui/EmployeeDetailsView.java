package com.example.application.humanresources.ui;

import com.example.application.common.ui.AppIcon;
import com.example.application.common.ui.Notifications;
import com.example.application.common.ui.SectionToolbar;
import com.example.application.humanresources.*;
import com.example.application.security.AppRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

@Route(value = "employees/:employeeId", layout = EmployeeListView.class)
@RolesAllowed(AppRoles.EMPLOYEE_READ)
class EmployeeDetailsView extends VerticalLayout implements AfterNavigationObserver, BeforeLeaveObserver, HasDynamicTitle {

    public static final String PARAM_EMPLOYEE_ID = "employeeId";
    private final EmployeeService employeeService;

    // TODO Make this view responsive
    // TODO Make this view accessible

    private final ValueSignal<Employee> employeeSignal = new ValueSignal<>(Employee.class);
    private final ValueSignal<Boolean> editModeSignal = new ValueSignal<>(false);
    private final ValueSignal<Integer> selectedTabIndexSignal = new ValueSignal<>(-1);
    private final TabSheet tabs;
    private final boolean canUpdate;

    EmployeeDetailsView(AuthenticationContext authenticationContext, EmployeeService employeeService, EmployeePictureService employeePictureService) {
        this.employeeService = employeeService;
        canUpdate = authenticationContext.hasRole(AppRoles.EMPLOYEE_UPDATE);

        // Create components
        var title = new H2();
        var avatar = new Avatar();

        var editButton = new Button("Edit", e -> edit());

        var saveButton = new Button("Save Changes", e -> saveChanges());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var discardButton = new Button("Discard Changes", e -> discardChanges());

        var closeButton = new Button();
        closeButton.getElement().appendChild(AppIcon.CLOSE.create().getElement()); // Until we get an icon-only button variant for Aura
        closeButton.addClickListener(e -> HumanResourcesNavigation.navigateToEmployeeList());

        tabs = new TabSheet();
        tabs.addSelectedChangeListener(e -> selectedTabIndexSignal.value(tabs.getIndexOf(e.getSelectedTab())));
        tabs.add(new Tab("Personal"), new PersonalTab());
        tabs.add(new Tab("Job"), new JobTab());
        tabs.add(new Tab("Emergency"), new EmergencyTab());
        tabs.add(new Tab("Documents"), new DocumentsTab());
        tabs.setSizeFull();

        // Layout components
        setSizeFull();
        var upperToolbar = new SectionToolbar(SectionToolbar.group(avatar, title), SectionToolbar.group(closeButton));
        upperToolbar.setPadding(false);
        add(upperToolbar);
        add(tabs);
        var lowerToolbar = new SectionToolbar(SectionToolbar.group(editButton, saveButton, discardButton));
        lowerToolbar.setPadding(false);
        add(lowerToolbar);

        // Populate components
        ComponentEffect.effect(this, () -> {
            var employee = employeeSignal.value();
            if (employee != null) {
                title.setText(PersonNameFormatter.firstLast().toFullName(employee.data().firstName(), employee.data().lastName()));
                avatar.setName(title.getText());
                avatar.setImageHandler(employeePictureService.findEmployeePicture(employee.id()));
            }
        });
        ComponentEffect.effect(this, () -> {
            var editable = getSelectedTabComponent() instanceof EditableTab;
            var editMode = editModeSignal.value();

            lowerToolbar.setVisible(canUpdate && editable);

            editButton.setVisible(!editMode);
            saveButton.setVisible(editMode);
            discardButton.setVisible(editMode);

            for (var i = 0; i < tabs.getTabCount(); i++) {
                var t = tabs.getTabAt(i);
                t.setEnabled(!editMode || t == tabs.getSelectedTab());
            }
        });
    }

    private Component getSelectedTabComponent() {
        var tab = tabs.getTabAt(selectedTabIndexSignal.value());
        return tabs.getComponent(tab);
    }

    private Optional<EmployeeListView> getEmployeeListView() {
        return getParent().filter(EmployeeListView.class::isInstance).map(EmployeeListView.class::cast);
    }

    private void edit() {
        editModeSignal.value(true);
    }

    private void saveChanges() {
        if (getSelectedTabComponent() instanceof EditableTab editableTab) {
            editableTab.save();
        }
    }

    private void discardChanges() {
        if (getSelectedTabComponent() instanceof EditableTab editableTab) {
            editableTab.discard();
        }
    }

    private interface EditableTab {
        void save();

        void discard();
    }

    private class PersonalTab extends VerticalLayout implements EditableTab {

        private final EmployeeDataForm form;

        PersonalTab() {
            form = new EmployeeDataForm();
            add(form);
            setPadding(false);
            setSpacing(false);

            ComponentEffect.effect(this, () -> {
                var employee = employeeSignal.value();
                if (employee != null) {
                    form.setFormDataObject(employee.data());
                }
                form.setReadOnly(!editModeSignal.value());
            });
        }

        @Override
        public void save() {
            form.getFormDataObject().ifPresent(employeeData -> {
                try {
                    var saved = employeeService.update(employeeSignal.value().withData(employeeData));
                    employeeSignal.value(saved);
                    editModeSignal.value(false);
                    getEmployeeListView().ifPresent(employeeListView -> employeeListView.onEmployeeUpdated(saved));
                } catch (OptimisticLockingFailureException e) {
                    Notifications.createCriticalNotification(AppIcon.PERSON_PLAY.create(AppIcon.Size.M),
                            "Another user has edited the employee. Please refresh and try again.",
                            NotificationVariant.LUMO_WARNING).open();
                }
            });
        }

        @Override
        public void discard() {
            form.setFormDataObject(employeeSignal.value().data());
            editModeSignal.value(false);
        }
    }

    private class JobTab extends VerticalLayout {
        JobTab() {
            add("This tab has not been implemented yet");
        }
    }

    private class EmergencyTab extends VerticalLayout {
        EmergencyTab() {
            add("This tab has not been implemented yet");
        }
    }

    private class DocumentsTab extends VerticalLayout {
        DocumentsTab() {
            add("This tab has not been implemented yet");
        }
    }

    @Override
    public String getPageTitle() {
        return "Employee Details - " + Optional.ofNullable(employeeSignal.value()).map(employee -> PersonNameFormatter.firstLast().toFullName(employee.data().firstName(), employee.data().lastName())).orElse("");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        editModeSignal.value(false);
        afterNavigationEvent.getRouteParameters()
                .getLong(PARAM_EMPLOYEE_ID)
                .map(EmployeeId::of)
                .flatMap(employeeService::findById)
                .ifPresentOrElse(employeeSignal::value, HumanResourcesNavigation::navigateToEmployeeList);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (editModeSignal.value()) {
            var continueNavigationAction = event.postpone();
            var confirmDialog = new ConfirmDialog("Unsaved changes", "There are unsaved changes. Do you want to discard or save them?", "Save", e -> {
                saveChanges();
                continueNavigationAction.proceed();
            }, "Discard", e -> continueNavigationAction.proceed(), "Cancel", e -> continueNavigationAction.cancel());
            confirmDialog.open();
        }
    }
}
