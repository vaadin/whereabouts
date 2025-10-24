package com.example.whereabouts.humanresources.ui;

import com.example.whereabouts.common.ui.AppIcon;
import com.example.whereabouts.common.ui.Badges;
import com.example.whereabouts.common.ui.Notifications;
import com.example.whereabouts.common.ui.SectionToolbar;
import com.example.whereabouts.humanresources.*;
import com.example.whereabouts.security.AppRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;
import java.util.Set;

/**
 * @see "Design decision: DD008-20251024-master-detail.md"
 */
@Route(value = "employees/:employeeId", layout = EmployeeListView.class)
@RolesAllowed(AppRoles.EMPLOYEE_READ)
class EmployeeDetailsView extends VerticalLayout implements AfterNavigationObserver, BeforeLeaveObserver, HasDynamicTitle {

    public static final String PARAM_EMPLOYEE_ID = "employeeId";
    private final EmployeeService employeeService;
    private final LocationService locationService;

    // TODO Make this view responsive
    // TODO Make this view accessible

    private final ValueSignal<Employee> employeeSignal = new ValueSignal<>(Employee.class);
    private final ValueSignal<EmploymentDetails> employmentDetailsSignal = new ValueSignal<>(EmploymentDetails.class);
    private final ValueSignal<Boolean> editModeSignal = new ValueSignal<>(false);
    private final ValueSignal<Integer> selectedTabIndexSignal = new ValueSignal<>(-1);
    private final TabSheet tabs;
    private final boolean canUpdate;

    EmployeeDetailsView(AuthenticationContext authenticationContext, EmployeeService employeeService,
                        EmployeePictureService employeePictureService, LocationService locationService) {
        this.employeeService = employeeService;
        this.locationService = locationService;
        canUpdate = authenticationContext.hasRole(AppRoles.EMPLOYEE_UPDATE);

        // Create components
        var employeeNameHeader = new H2();
        var employeeJobTitleHeader = Badges.create("");
        var avatar = new Avatar();

        var editButton = new Button("Edit", e -> edit());

        var saveButton = new Button("Save Changes", e -> saveChanges());
        saveButton.addThemeName("primary");

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
        var upperToolbar = new SectionToolbar(SectionToolbar.group(avatar, employeeNameHeader, employeeJobTitleHeader), SectionToolbar.group(closeButton));
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
                var fullName = PersonNameFormatter.firstLast().toFullName(employee.data());
                employeeNameHeader.setText(fullName + " " + employee.data().homeAddress().country().flagUnicode());
                avatar.setName(fullName);
                avatar.setImageHandler(employeePictureService.findPicture(employee.id()));
                var details = employeeService.findDetailsById(employee.id()).orElse(null);
                employmentDetailsSignal.value(details);
            } else {
                employmentDetailsSignal.value(null);
            }
        });
        ComponentEffect.effect(this, () -> {
            var details = employmentDetailsSignal.value();
            employeeJobTitleHeader.setText(details == null ? "" : details.data().jobTitle());
            employeeJobTitleHeader.setVisible(details != null);
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
                    getEmployeeListView().ifPresent(employeeListView -> employeeListView.onEmployeeUpdated(saved.id()));
                } catch (OptimisticLockingFailureException e) {
                    Notifications.createOptimisticLockingFailureNotification().open();
                }
            });
        }

        @Override
        public void discard() {
            form.setFormDataObject(employeeSignal.value().data());
            editModeSignal.value(false);
        }
    }

    private class JobTab extends VerticalLayout implements EditableTab {

        // TODO Could this tab be made more elegant?

        private final EmploymentDetailsDataForm form;

        JobTab() {
            form = new EmploymentDetailsDataForm(
                    locationService::findReferencesBySearchTerm,
                    locationService::getReferenceById,
                    (pageable, searchTerm) -> employeeService.findReferencesByFilter(pageable, new EmployeeFilter(searchTerm, Set.of(EmploymentStatus.ACTIVE), Set.of())),
                    employeeService::findReferenceById
            );
            add(form);
            setPadding(false);
            setSpacing(false);

            ComponentEffect.effect(this, () -> {
                var details = employmentDetailsSignal.value();
                if (details != null) {
                    form.setFormDataObject(details.data());
                } else {
                    form.setFormDataObject(null);
                }
                form.setReadOnly(!editModeSignal.value());
            });
        }

        @Override
        public void save() {
            form.getFormDataObject().ifPresent(detailsData -> {
                try {
                    var saved = save(detailsData);
                    employmentDetailsSignal.value(saved);
                    editModeSignal.value(false);
                    getEmployeeListView().ifPresent(employeeListView -> employeeListView.onEmployeeUpdated(saved.id()));
                } catch (OptimisticLockingFailureException e) {
                    Notifications.createOptimisticLockingFailureNotification().open();
                }
            });
        }

        private EmploymentDetails save(EmploymentDetailsData data) {
            var existing = employmentDetailsSignal.peek();
            if (existing != null) {
                return employeeService.updateDetails(existing.withData(data));
            } else {
                return employeeService.insertDetails(employeeSignal.peek().id(), data);
            }
        }

        @Override
        public void discard() {
            form.setFormDataObject(Optional.ofNullable(employmentDetailsSignal.value()).map(EmploymentDetails::data).orElse(null));
            editModeSignal.value(false);
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
        return "Employee Details - " + Optional.ofNullable(employeeSignal.value())
                .map(employee -> PersonNameFormatter.firstLast().toFullName(employee.data()))
                .orElse("");
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
