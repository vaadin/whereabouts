package com.example.application.projects.ui;

import com.example.application.common.ui.AppIcon;
import com.example.application.common.ui.MainLayout;
import com.example.application.common.ui.SectionToolbar;
import com.example.application.projects.ProjectId;
import com.example.application.projects.ProjectListItem;
import com.example.application.projects.ProjectService;
import com.example.application.projects.ProjectSortableProperty;
import com.example.application.security.AppRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.RolesAllowed;

@ParentLayout(MainLayout.class)
@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects")
@Menu(order = 10, icon = "icons/folder_check_2.svg", title = "Projects")
@RolesAllowed(AppRoles.PROJECT_READ)
class ProjectListView extends MasterDetailLayout implements AfterNavigationObserver {

    private final ProjectService projectService;
    private final ProjectList projectList;
    private final boolean canCreate;

    ProjectListView(AuthenticationContext authenticationContext, ProjectService projectService) {
        this.projectService = projectService;

        canCreate = authenticationContext.hasRole(AppRoles.PROJECT_CREATE);

        projectList = new ProjectList();

        setMaster(projectList);
        setMasterSize(400, Unit.PIXELS);
        setDetailMinSize(400, Unit.PIXELS);
        addBackdropClickListener(event -> projectList.grid.deselectAll());
    }

    void onProjectUpdated(ProjectId projectId) {
        refreshProject(projectId);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        refresh();
        event.getRouteParameters()
                .getLong(ProjectDetailsView.PARAM_PROJECT_ID)
                .map(ProjectId::of)
                .flatMap(projectService::findProjectListItemById)
                .ifPresentOrElse(projectList.grid::select, projectList.grid::deselectAll);
    }

    private void refresh() {
        projectList.grid.getDataProvider().refreshAll();
    }

    private void refreshProject(ProjectId projectId) {
        projectService.findProjectListItemById(projectId)
                .ifPresentOrElse(
                        projectList.grid.getDataProvider()::refreshItem,
                        projectList.grid.getDataProvider()::refreshAll
                );
    }

    private void showAll() {
        projectList.searchField.clear();
    }

    private void addProject() {
        var dialog = new AddProjectDialog(fdo -> {
            var projectId = projectService.insert(fdo);
            ProjectsNavigation.navigateToProjectDetails(projectId);
        });
        dialog.open();
    }

    private class ProjectList extends Section {

        private final Grid<ProjectListItem> grid;
        private final TextField searchField;

        ProjectList() {
            var title = new H3("Projects");

            var addProjectButton = new Button("Add Project", VaadinIcon.PLUS.create(), event -> addProject());
            addProjectButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addProjectButton.setVisible(canCreate);

            searchField = new TextField();
            searchField.setPlaceholder("Search");
            searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
            searchField.addClassNames(Flex.GROW);
            searchField.setValueChangeMode(ValueChangeMode.LAZY);
            searchField.addValueChangeListener(event -> refresh());

            var sortField = new Select<ProjectSortOrder>();
            sortField.setItems(ProjectSortOrder.values());
            sortField.setValue(ProjectSortOrder.NAME_ASC);
            sortField.setItemLabelGenerator(ProjectSortOrder::getDisplayName);
            sortField.addClassNames(Flex.GROW);
            sortField.addValueChangeListener(event -> refresh());

            grid = new Grid<>();
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.setItems((CallbackDataProvider.FetchCallback<ProjectListItem, Void>) query ->
                    projectService.findProjectListItems(searchField.getValue(), query.getLimit(), query.getOffset(),
                            sortField.getValue().getSortOrder()));
            grid.addColumn(new ComponentRenderer<>(this::createProjectCard));
            grid.setSizeFull();
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addSelectionListener(event -> event.getFirstSelectedItem().map(ProjectListItem::projectId)
                    .ifPresentOrElse(ProjectsNavigation::navigateToProjectDetails, ProjectsNavigation::navigateToProjectList));
            grid.setEmptyStateComponent(new ProjectListEmptyComponent());

            setSizeFull();
            addClassNames("project-list", Display.FLEX, FlexDirection.COLUMN);
            var toolbar = new SectionToolbar(SectionToolbar.group(new DrawerToggle(), title), addProjectButton).withRow(searchField).withRow(sortField);
            add(toolbar, grid);
        }

        private Component createProjectCard(ProjectListItem projectListItem) {
            var card = new Card();
            card.setTitle(projectListItem.projectName());

            var tasks = new Span(
                    projectListItem.tasks() == 1 ? "1 task" : "%d tasks".formatted(projectListItem.tasks()));
            tasks.addClassNames(TextColor.SECONDARY, FontSize.SMALL);

            var assignees = new Span(projectListItem.assignees() == 1
                    ? "1 assignee"
                    : "%d assignees".formatted(projectListItem.assignees()));
            assignees.addClassNames(TextColor.SECONDARY, FontSize.SMALL);

            card.addToFooter(tasks, assignees);
            return card;
        }
    }

    private class ProjectListEmptyComponent extends VerticalLayout {
        ProjectListEmptyComponent() {
            var icon = AppIcon.FOLDER_CHECK_2.create();
            icon.setSize("60px");
            var title = new H4("No projects found");
            var instruction = new Span("Change the search criteria or add a project");

            var addProject = new Button("Add Project", VaadinIcon.PLUS.create(), event -> addProject());
            addProject.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addProject.setVisible(canCreate);

            var showAll = new Button("Show All", AppIcon.FILTER_NONE.create(), event -> showAll());
            showAll.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            add(icon, title, instruction, new HorizontalLayout(addProject, showAll));

            setSizeFull();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
        }
    }

    private enum ProjectSortOrder {
        NAME_ASC("Sort by name (A-Z)", new SortOrder<>(ProjectSortableProperty.NAME, SortDirection.ASCENDING)),
        NAME_DESC("Sort by name (Z-A)", new SortOrder<>(ProjectSortableProperty.NAME, SortDirection.DESCENDING));
        // TODO add more options

        private final String displayName;
        private final SortOrder<ProjectSortableProperty> sortOrder;

        ProjectSortOrder(String displayName, SortOrder<ProjectSortableProperty> sortOrder) {
            this.displayName = displayName;
            this.sortOrder = sortOrder;
        }

        String getDisplayName() {
            return displayName;
        }

        SortOrder<ProjectSortableProperty> getSortOrder() {
            return sortOrder;
        }
    }
}
