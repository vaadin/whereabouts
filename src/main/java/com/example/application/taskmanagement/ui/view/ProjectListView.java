package com.example.application.taskmanagement.ui.view;

import com.example.application.base.ui.component.SectionToolbar;
import com.example.application.base.ui.view.MainLayout;
import com.example.application.security.AppRoles;
import com.example.application.taskmanagement.service.ProjectService;
import com.example.application.taskmanagement.dto.ProjectListItem;
import com.example.application.taskmanagement.ui.component.AddProjectDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ParentLayout(MainLayout.class)
@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects")
@Menu(order = 0, icon = "icons/folder_check_2.svg", title = "Projects")
@PermitAll
class ProjectListView extends MasterDetailLayout implements AfterNavigationObserver {

    private final ProjectService projectService;
    private final ProjectList projectList;
    private final boolean isAdmin; // Only admins can add projects

    ProjectListView(AuthenticationContext authenticationContext, ProjectService projectService) {
        this.projectService = projectService;

        isAdmin = authenticationContext.hasRole(AppRoles.ADMIN);

        projectList = new ProjectList();

        setMaster(projectList);
        setMasterSize(400, Unit.PIXELS);
        setDetailMinSize(300, Unit.PIXELS);
        addBackdropClickListener(event -> projectList.grid.deselectAll());

        addClassNames("project-list-view");

        addListener(ProjectTasksChangedEvent.class, event -> refreshProject(event.getProjectId()));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        refresh();
        event.getRouteParameters()
                .getLong(TaskListView.PARAM_PROJECT_ID)
                .flatMap(projectService::findProjectListItemById)
                .ifPresentOrElse(projectList.grid::select, projectList.grid::deselectAll);
    }

    private void refresh() {
        projectList.grid.getDataProvider().refreshAll();
    }

    private void refreshProject(long projectId) {
        projectService.findProjectListItemById(projectId).ifPresentOrElse(
                projectList.grid.getDataProvider()::refreshItem, projectList.grid.getDataProvider()::refreshAll);
    }

    private void showAll() {
        projectList.searchField.clear();
    }

    private void addProject() {
        var dialog = new AddProjectDialog(fdo -> {
            var projectId = projectService.saveProject(fdo).requireId();
            TaskListView.showTasksForProjectId(projectId);
        });
        dialog.open();
    }

    public static void showProjects() {
        UI.getCurrent().navigate(ProjectListView.class);
    }

    // TODO Requires https://github.com/vaadin/web-components/issues/9797
    private static class NoProjectSelection extends Div {
        NoProjectSelection() {
            var icon = new SvgIcon("icons/list_alt_check.svg");
            icon.setSize("60px");
            var title = new H4("No project selected");
            var instruction = new Span("Select a project to get started");

            setSizeFull();
            addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.CENTER, JustifyContent.CENTER);
            var centerDiv = new Div(icon, title, instruction);
            centerDiv.addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.CENTER, Gap.SMALL);
            add(centerDiv);
        }
    }

    private class ProjectList extends Section {

        private final Grid<ProjectListItem> grid;
        private final TextField searchField;

        ProjectList() {
            var title = new H3("Projects");

            var addProjectButton = new Button("Add Project", VaadinIcon.PLUS.create(), event -> addProject());
            addProjectButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addProjectButton.setVisible(isAdmin);

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
            grid.setItemsPageable(pageable -> projectService.findProjectListItems(searchField.getValue(),
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortField.getValue().getSort())));
            grid.addColumn(new ComponentRenderer<>(this::createProjectCard));
            grid.setSizeFull();
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addSelectionListener(event -> event.getFirstSelectedItem().map(ProjectListItem::projectId)
                    .ifPresentOrElse(TaskListView::showTasksForProjectId, ProjectListView::showProjects));
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
            var icon = new SvgIcon("icons/folder_check_2.svg");
            icon.setSize("60px");
            var title = new H4("No projects found");
            var instruction = new Span("Change the search criteria or add a project");

            var addProject = new Button("Add Project", VaadinIcon.PLUS.create(), event -> addProject());
            addProject.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addProject.setVisible(isAdmin);

            var showAll = new Button("Show All", new SvgIcon("icons/filter_none.svg"), event -> showAll());
            showAll.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            add(icon, title, instruction, new HorizontalLayout(addProject, showAll));

            setSizeFull();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
        }
    }

    private enum ProjectSortOrder {
        NAME_ASC("Sort by name (A-Z)", Sort.by(Sort.Direction.ASC, "name")),
        NAME_DESC("Sort by name (Z-A)", Sort.by(Sort.Direction.DESC, "name"))
        // TODO add more options
        ;

        private final String displayName;
        private final Sort sort;

        ProjectSortOrder(String displayName, Sort sort) {
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
