package com.example.application.taskmanagement.ui.view;

import com.example.application.base.ui.component.SectionToolbar;
import com.example.application.base.ui.component.ViewHeader;
import com.example.application.base.ui.view.MainLayout;
import com.example.application.taskmanagement.ProjectService;
import com.example.application.taskmanagement.dto.ProjectListItem;
import com.example.application.taskmanagement.ui.component.AddProjectDialog;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
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
@PageTitle("Tasks")
@Menu(order = 0, icon = "icons/list_alt_check.svg", title = "Tasks") // TODO Fix icon
@PermitAll
class ProjectListView extends Div implements RouterLayout, AfterNavigationObserver {

    private final MasterDetailLayout masterDetailLayout;
    private final ProjectService projectService;
    private final ProjectList projectList;

    ProjectListView(AuthenticationContext authenticationContext, ProjectService projectService) {
        this.projectService = projectService;

        masterDetailLayout = new MasterDetailLayout();
        projectList = new ProjectList();

        setSizeFull();
        addClassNames("project-list-view", Display.FLEX, FlexDirection.COLUMN);
        add(new ViewHeader(authenticationContext, "Tasks"));
        add(masterDetailLayout);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        masterDetailLayout.showRouterLayoutContent(content);
    }

    @Override
    public void removeRouterLayoutContent(HasElement oldContent) {
        masterDetailLayout.removeRouterLayoutContent(oldContent);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        if (projectService.hasProjects()) {
            masterDetailLayout.setMaster(projectList);
            masterDetailLayout.setMasterSize(300, Unit.PIXELS);
            refresh();
            event.getRouteParameters().getLong(TaskListView.PARAM_PROJECT_ID)
                    .flatMap(projectService::findProjectListItemById)
                    .ifPresentOrElse(
                            this::select,
                            this::deselectAll
                    );
        } else {
            masterDetailLayout.setMaster(new NoProjects());
            masterDetailLayout.setMasterSize(null);
        }
    }

    private void select(ProjectListItem project) {
        projectList.grid.select(project);
    }

    private void deselectAll() {
        projectList.grid.deselectAll();
        masterDetailLayout.setDetail(new NoProjectSelection());
    }

    void refresh() {
        projectList.grid.getDataProvider().refreshAll();
    }

    private class NoProjects extends Div {
        NoProjects() {
            var icon = new SvgIcon("icons/list_alt_check.svg");
            icon.setSize("60px");
            var title = new H4("No projects yet");
            var instruction = new Span("Add a project to get started");
            var addProject = new Button("Add Project", VaadinIcon.PLUS.create(), event -> addProject());
            addProject.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            setSizeFull();
            addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.CENTER, JustifyContent.CENTER);
            var centerDiv = new Div(icon, title, instruction, addProject);
            centerDiv.addClassNames(Display.FLEX, FlexDirection.COLUMN, AlignItems.CENTER, Gap.SMALL);
            add(centerDiv);
        }
    }

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

        ProjectList() {
            var title = new H3("Projects");

            var addProjectButton = new Button("Add Project", VaadinIcon.PLUS.create(), event -> addProject());
            addProjectButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            var searchField = new TextField();
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
            grid.setItemsPageable(pageable -> projectService.findProjectListItems(
                    searchField.getValue(),
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortField.getValue().getSort())
            ));
            grid.addColumn(new ComponentRenderer<>(ProjectListItemPanel::new));
            grid.setSizeFull();
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addSelectionListener(event ->
                    event.getFirstSelectedItem()
                            .map(ProjectListItem::projectId)
                            .ifPresentOrElse(
                                    TaskListView::showTasksForProjectId,
                                    ProjectListView::showProjects
                            ));

            setSizeFull();
            addClassNames("project-list", Display.FLEX, FlexDirection.COLUMN);
            var toolbar = new SectionToolbar(title, addProjectButton).withRow(searchField).withRow(sortField);
            add(toolbar, grid);
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

    private static class ProjectListItemPanel extends Div {

        public ProjectListItemPanel(ProjectListItem projectListItem) {
            var name = new H4(projectListItem.projectName());
            var tasks = new Span("No tasks"); // TODO Fix me
            tasks.addClassNames(TextColor.SECONDARY, FontSize.SMALL);
            var assignees = new Span("No assignees"); // TODO Fix me
            assignees.addClassNames(TextColor.SECONDARY, FontSize.SMALL);
            var footer = new Div(tasks, assignees);
            footer.addClassNames(Display.FLEX, FlexDirection.ROW, JustifyContent.BETWEEN);

            addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM, Padding.Vertical.MEDIUM);
            add(name, footer);
        }
    }

    private void addProject() {
        var dialog = new AddProjectDialog(fdo -> {
            var projectId = projectService.createProject(fdo);
            TaskListView.showTasksForProjectId(projectId);
        });
        dialog.open();
    }

    public static void showProjects() {
        UI.getCurrent().navigate(ProjectListView.class);
    }
}
