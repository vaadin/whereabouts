package com.example.application.humanresources.ui;

import com.example.application.common.ui.AppIcon;
import com.example.application.common.ui.SectionToolbar;
import com.example.application.humanresources.*;
import com.example.application.security.AppRoles;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@Route(value = "employees/:employeeId", layout = EmployeeListView.class)
@RolesAllowed(AppRoles.EMPLOYEE_READ)
class EmployeeDetailsView extends VerticalLayout implements AfterNavigationObserver, HasDynamicTitle {

    public static final String PARAM_EMPLOYEE_ID = "employeeId";
    private final EmployeeService employeeService;
    private final EmployeePictureService employeePictureService;

    // TODO Style this view (using as little custom CSS as possible)
    // TODO Make this view responsive
    // TODO Make this view accessible

    private final ValueSignal<Employee> employeeSignal = new ValueSignal<>(Employee.class);

    EmployeeDetailsView(AuthenticationContext authenticationContext, EmployeeService employeeService,
                        EmployeePictureService employeePictureService) {
        this.employeeService = employeeService;
        this.employeePictureService = employeePictureService;
        var isAdmin = authenticationContext.hasRole(AppRoles.ADMIN);

        // Create components
        var title = new H2();
        var avatar = new Avatar();
        var editButton = new Button("Edit");
        editButton.setVisible(isAdmin);
        // TODO Delete button?
        var closeButton = new Button(AppIcon.CLOSE.create(), e -> HumanResourcesNavigation.navigateToEmployeeList());
        closeButton.addThemeVariants(ButtonVariant.LUMO_ICON);

        var tabs = new TabSheet();
        tabs.add(new Tab("Personal"), new PersonalTab());
        tabs.add(new Tab("Job"), new JobTab());
        tabs.setSizeFull();

        // Layout components
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        add(new SectionToolbar(SectionToolbar.group(avatar, title), SectionToolbar.group(editButton, closeButton)));
        add(tabs);

        // Populate components
        ComponentEffect.effect(this, () -> {
            var employee = employeeSignal.value();
            if (employee != null) {
                title.setText(PersonNameFormatter.firstLast().toFullName(employee.data().firstName(), employee.data().lastName()));
                avatar.setName(title.getText());
                avatar.setImageHandler(employeePictureService.findEmployeePicture(employee.id()));
                // TODO Add the rest of the data
            }
        });
    }

    private class PersonalTab extends VerticalLayout {

    }

    private class JobTab extends VerticalLayout {

    }

    @Override
    public String getPageTitle() {
        return "Employee Details - " + Optional.ofNullable(employeeSignal.value())
                .map(employee -> PersonNameFormatter.firstLast().toFullName(employee.data().firstName(), employee.data().lastName()))
                .orElse("");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        afterNavigationEvent.getRouteParameters()
                .getLong(PARAM_EMPLOYEE_ID)
                .map(EmployeeId::of)
                .flatMap(employeeService::findById)
                .ifPresentOrElse(employeeSignal::value, HumanResourcesNavigation::navigateToEmployeeList);
    }
}
