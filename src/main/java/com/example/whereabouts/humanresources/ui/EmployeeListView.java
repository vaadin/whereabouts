package com.example.whereabouts.humanresources.ui;

import com.example.whereabouts.MainLayout;
import com.example.whereabouts.common.ui.AppIcon;
import com.example.whereabouts.common.ui.SectionToolbar;
import com.example.whereabouts.humanresources.*;
import com.example.whereabouts.security.AppRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * @see "Design decision: DD008-20251024-master-detail.md"
 */
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
        employeeList.setWidth(400, Unit.PIXELS); // Workaround for https://github.com/vaadin/web-components/issues/10318
        setDetailMinSize(400, Unit.PIXELS);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        afterNavigationEvent.getRouteParameters()
                .getLong(EmployeeDetailsView.PARAM_EMPLOYEE_ID)
                .map(EmployeeId::new)
                .flatMap(employeeService::findReferenceById)
                .ifPresentOrElse(employeeList.grid::select, employeeList.grid::deselectAll);
    }

    void onEmployeeUpdated(EmployeeId employeeId) {
        var isSelected = employeeList.grid
                .getSelectionModel()
                .getFirstSelectedItem()
                .filter(row -> row.id().equals(employeeId))
                .isPresent();
        employeeList.grid.getDataProvider().refreshAll();
        if (isSelected) {
            employeeService.findReferenceById(employeeId).ifPresent(employeeList.grid::select);
        }
    }

    private class EmployeeList extends VerticalLayout {

        private final Grid<EmployeeReference> grid;
        private final ValueSignal<EmployeeFilter> filterSignal = new ValueSignal<>(EmployeeFilter.empty());

        EmployeeList(boolean canCreate) {
            var title = new H1("Employees");

            var addEmployeeButton = new Button("Add Employee");
            addEmployeeButton.setVisible(canCreate);

            var searchField = new TextField();
            searchField.setPlaceholder("Search");
            searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
            searchField.setValueChangeMode(ValueChangeMode.LAZY);
            searchField.setWidthFull();

            var sortField = new Select<EmployeeSortOrder>();
            sortField.setItems(EmployeeSortOrder.values());
            sortField.setValue(EmployeeSortOrder.LAST_NAME_ASC);
            sortField.setItemLabelGenerator(EmployeeSortOrder::getDisplayName);
            sortField.getStyle().setFlexGrow("1");

            grid = new Grid<>();
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.setItemsPageable(pageable -> employeeService.findReferencesByFilter(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortField.getValue().getSort()), filterSignal.peek()));
            grid.addColumn(new ComponentRenderer<>(employee -> EmployeeTitleCard.of(
                    employee,
                    employeePictureService::findPicture)
            ));
            grid.setSizeFull();
            grid.addThemeName("no-border");

            // Add listeners
            searchField.addValueChangeListener(e ->
                    filterSignal.update(old -> old.withSearchTerm(e.getValue())));
            sortField.addValueChangeListener(e -> grid.getDataProvider().refreshAll());
            grid.addSelectionListener(e -> e.getFirstSelectedItem()
                    .map(EmployeeReference::id)
                    .ifPresentOrElse(
                            HumanResourcesNavigation::navigateToEmployeeDetails,
                            HumanResourcesNavigation::navigateToEmployeeList
                    ));
            addEmployeeButton.addClickListener(e -> addEmployee());
            ComponentEffect.effect(this, () -> {
                // Refresh the grid whenever the filter changes
                filterSignal.value();
                grid.getDataProvider().refreshAll();
            });

            // Layout components
            var toolbar = new SectionToolbar(
                    SectionToolbar.group(new DrawerToggle(), title),
                    addEmployeeButton
            ).withRow(searchField).withRow(sortField, createFilterMenu());
            toolbar.getStyle().setBorderBottom("1px solid var(--vaadin-border-color-secondary)");
            setSizeFull();
            setPadding(false);
            setSpacing(false);
            getStyle().setOverflow(Style.Overflow.HIDDEN);

            add(toolbar, grid);
        }

        private void addEmployee() {
            var dialog = new AddEmployeeDialog(employeeData -> {
                var id = employeeService.insert(employeeData);
                grid.getDataProvider().refreshAll();
                HumanResourcesNavigation.navigateToEmployeeDetails(id);
            });
            dialog.open();
        }

        private Component createFilterMenu() {
            var menuBar = new MenuBar();
            var item = menuBar.addItem(AppIcon.FILTER_LIST.create(), "Filters");
            var subMenu = item.getSubMenu();

            var statusFormatter = EmploymentStatusFormatter.ofLocale(getLocale());
            for (var status : EmploymentStatus.values()) {
                subMenu.addItem(statusFormatter.getDisplayName(status), event -> {
                    if (event.getSource().isChecked()) {
                        filterSignal.update(old -> old.withStatus(status));
                    } else {
                        filterSignal.update(old -> old.withoutStatus(status));
                    }
                }).setCheckable(true);
            }
            subMenu.addSeparator();
            var typeFormatter = EmploymentTypeFormatter.ofLocale(getLocale());
            for (var type : EmploymentType.values()) {
                subMenu.addItem(typeFormatter.getDisplayName(type), event -> {
                    if (event.getSource().isChecked()) {
                        filterSignal.update(old -> old.withType(type));
                    } else {
                        filterSignal.update(old -> old.withoutType(type));
                    }
                }).setCheckable(true);
            }
            return menuBar;
        }
    }

    private enum EmployeeSortOrder {
        LAST_NAME_ASC("Sort by last name (A-Z)", Sort.by(Sort.Direction.ASC, EmployeeSortableProperty.LAST_NAME.name(), EmployeeSortableProperty.FIRST_NAME.name())),
        LAST_NAME_DESC("Sort by last name (Z-A)", Sort.by(Sort.Direction.DESC, EmployeeSortableProperty.LAST_NAME.name(), EmployeeSortableProperty.FIRST_NAME.name())),
        FIRST_NAME_ASC("Sort by first name (A-Z)", Sort.by(Sort.Direction.ASC, EmployeeSortableProperty.FIRST_NAME.name(), EmployeeSortableProperty.LAST_NAME.name())),
        FIRST_NAME_DESC("Sort by first name (Z-A)", Sort.by(Sort.Direction.DESC, EmployeeSortableProperty.FIRST_NAME.name(), EmployeeSortableProperty.LAST_NAME.name()));

        private final String displayName;
        private final Sort sort;

        EmployeeSortOrder(String displayName, Sort sort) {
            this.displayName = displayName;
            this.sort = sort;
        }

        String getDisplayName() {
            return displayName;
        }

        Sort getSort() {
            return sort;
        }
    }
}
