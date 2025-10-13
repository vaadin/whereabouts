package com.example.application.humanresources.ui;

import com.example.application.common.ui.MainLayout;
import com.example.application.common.ui.SectionToolbar;
import com.example.application.humanresources.*;
import com.example.application.security.AppRoles;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

@ParentLayout(MainLayout.class)
@Route(value = "employees", layout = MainLayout.class)
@PageTitle("Employees")
@Menu(order = 1, title = "Employees", icon = "icons/diversity.svg")
@RolesAllowed(AppRoles.EMPLOYEE_READ)
class EmployeeListView extends MasterDetailLayout implements AfterNavigationObserver {

    private final EmployeeService employeeService;
    private final EmployeePictureService employeePictureService;
    private final EmployeeList employeeList;

    EmployeeListView(AuthenticationContext authenticationContext, EmployeeService employeeService, EmployeePictureService employeePictureService) {
        this.employeeService = employeeService;
        this.employeePictureService = employeePictureService;
        var canCreate = authenticationContext.hasRole(AppRoles.EMPLOYEE_CREATE);
        this.employeeList = new EmployeeList(canCreate);

        // Add listeners
        addBackdropClickListener(e -> employeeList.grid.deselectAll());

        // Layout components
        setMaster(employeeList);
        setMasterSize(400, Unit.PIXELS);
        setDetailMinSize(400, Unit.PIXELS);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        afterNavigationEvent.getRouteParameters()
                .getLong(EmployeeDetailsView.PARAM_EMPLOYEE_ID)
                .map(EmployeeId::of)
                .flatMap(employeeService::getEmployeeById)
                .ifPresentOrElse(employeeList.grid::select, employeeList.grid::deselectAll);
    }

    void onEmployeeUpdated(Employee employee) {
        var isSelected = employeeList.grid
                .getSelectionModel()
                .getFirstSelectedItem()
                .filter(row -> row.id().equals(employee.id()))
                .isPresent();
        employeeList.grid.getDataProvider().refreshAll();
        if (isSelected) {
            employeeService.getEmployeeById(employee.id()).ifPresent(employeeList.grid::select);
        }
    }

    private class EmployeeList extends VerticalLayout {

        private final Grid<EmployeeReference> grid;

        EmployeeList(boolean canCreate) {
            var title = new H1("Employees");

            var addEmployeeButton = new Button("Add Employee", VaadinIcon.PLUS.create());
            addEmployeeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addEmployeeButton.setVisible(canCreate);

            var searchField = new TextField();
            searchField.setPlaceholder("Search");
            searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
            searchField.setValueChangeMode(ValueChangeMode.LAZY);
            searchField.setWidthFull();

            // TODO Add sort

            grid = new Grid<>();
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.setItemsPageable(pageable -> employeeService.findEmployees(searchField.getValue(), pageable));
            grid.addColumn(new ComponentRenderer<>(employee -> EmployeeTitleCard.of(
                    employee,
                    employeePictureService::findEmployeePicture)
            ));
            grid.setSizeFull();
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

            // Add listeners
            searchField.addValueChangeListener(e -> grid.getDataProvider().refreshAll());
            grid.addSelectionListener(e -> e.getFirstSelectedItem()
                    .map(EmployeeReference::id)
                    .ifPresentOrElse(
                            HumanResourcesNavigation::navigateToEmployeeDetails,
                            HumanResourcesNavigation::navigateToEmployeeList
                    ));
            addEmployeeButton.addClickListener(e -> addEmployee());

            // Layout components
            var toolbar = new SectionToolbar(
                    SectionToolbar.group(new DrawerToggle(), title),
                    addEmployeeButton
            ).withRow(searchField);
            setSizeFull();
            setPadding(false);
            setSpacing(false);
            getStyle().setOverflow(Style.Overflow.HIDDEN);

            add(toolbar, grid);
        }
    }

    private void addEmployee() {
        // TODO Implement me!
    }
}
