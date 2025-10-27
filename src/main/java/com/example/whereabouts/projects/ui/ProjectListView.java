package com.example.whereabouts.projects.ui;

import com.example.whereabouts.MainLayout;
import com.example.whereabouts.common.ui.AppIcon;
import com.example.whereabouts.common.ui.SectionToolbar;
import com.example.whereabouts.projects.*;
import com.example.whereabouts.security.AppRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@ParentLayout(MainLayout.class)
@Route(value = "projects", layout = MainLayout.class)
@PageTitle("Projects")
@Menu(order = 10, icon = "icons/folder_check_2.svg", title = "Projects")
@RolesAllowed(AppRoles.PROJECT_READ)
class ProjectListView extends MasterDetailLayout implements AfterNavigationObserver {

    final static class ViewModel {

        private final ProjectService projectService;
        final DataProvider<ProjectListItem, Void> projects;
        final ValueSignal<ProjectSortOrder> sortOrder = new ValueSignal<>(ProjectSortOrder.NAME_ASC);
        final ValueSignal<String> searchTerm = new ValueSignal<>("");
        final ValueSignal<ProjectId> selectedProjectId = new ValueSignal<>(ProjectId.class);

        ViewModel(Component owner, ProjectService projectService) {
            this.projectService = projectService;
            projects = new CallbackDataProvider<>(
                    query ->
                            projectService.findProjectListItems(
                                    searchTerm.peek(),
                                    query.getLimit(), query.getOffset(),
                                    sortOrder.peek().getSortOrder()
                            ),
                    query -> {
                        throw new UnsupportedOperationException("Count not supported");
                    },
                    ProjectListItem::projectId
            );
            ComponentEffect.effect(owner, () -> {
                // TODO This is a workaround until we get better API support. The data provider is not an effect,
                //  nor is it a component, so it can't refresh itself when the sortOrder or searchTerm changes,
                //  even though those signals are used in it.
                sortOrder.value();
                searchTerm.value();
                projects.refreshAll();
            });
            ComponentEffect.effect(owner, () -> {
                // TODO This effect, in combination with afterNavigation() further down, keeps the projectId route
                //  parameter and the selectedProjectId signal in sync. We should get a proper API for this.
                Optional.ofNullable(selectedProjectId.value()).ifPresentOrElse(
                        ProjectsNavigation::navigateToProjectDetails,
                        ProjectsNavigation::navigateToProjectList
                );
            });
        }

        void addProject(ProjectData newProjectData) {
            var projectId = projectService.insert(newProjectData);
            projects.refreshAll();
            selectedProjectId.value(projectId);
        }
    }

    final ViewModel viewModel;
    private final ProjectService projectService;

    ProjectListView(AuthenticationContext authenticationContext, ProjectService projectService) {
        this.projectService = projectService;
        this.viewModel = new ViewModel(this, projectService);
        var canCreate = authenticationContext.hasRole(AppRoles.PROJECT_CREATE);

        var projectList = createProjectList(canCreate);
        setMaster(projectList);
        setMasterSize(400, Unit.PIXELS);
        projectList.setWidth(400, Unit.PIXELS); // TODO Workaround for https://github.com/vaadin/web-components/issues/10318
        setDetailMinSize(400, Unit.PIXELS);
        addBackdropClickListener(e
                -> viewModel.selectedProjectId.value(null));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewModel.selectedProjectId.value(event.getRouteParameters()
                .getLong(ProjectDetailsView.PARAM_PROJECT_ID)
                .map(ProjectId::new)
                .orElse(null));
    }

    private void openAddProjectDialog() {
        new AddProjectDialog(viewModel::addProject).open();
    }

    private VerticalLayout createProjectList(boolean canCreate) {
        // TODO I'm not sure what I feel about this style of creating views
        return new VerticalLayout(
                new SectionToolbar(
                        SectionToolbar.group(
                                new DrawerToggle(),
                                new H1("Projects")
                        ),
                        new Button("Add Project", e -> openAddProjectDialog()) {{
                            setVisible(canCreate);
                        }}) {{
                    withRow(new TextField() {{
                        setPlaceholder("Search");
                        setPrefixComponent(VaadinIcon.SEARCH.create());
                        setWidthFull();
                        setValueChangeMode(ValueChangeMode.LAZY);
                        // TODO Workaround until we get bind support into the API:
                        addValueChangeListener(e -> viewModel.searchTerm.value(e.getValue()));
                        ComponentEffect.effect(this, () -> setValue(viewModel.searchTerm.value()));
                    }});
                    withRow(new Select<ProjectSortOrder>() {
                        {
                            setItems(ProjectSortOrder.values());
                            setItemLabelGenerator(ProjectSortOrder::getDisplayName);
                            setWidthFull();
                            // TODO Workaround until we get bind support into the API:
                            addValueChangeListener(e -> viewModel.sortOrder.value(e.getValue()));
                            ComponentEffect.effect(this, () -> setValue(viewModel.sortOrder.value()));
                        }
                    });
                    getStyle().setBorderBottom("1px solid var(--vaadin-border-color-secondary)");
                }},
                new Grid<ProjectListItem>() {{
                    setSelectionMode(Grid.SelectionMode.SINGLE);
                    setDataProvider(viewModel.projects);
                    getLazyDataView().setItemCountUnknown(); // Because we don't provide a count callback
                    addColumn(new ComponentRenderer<>(ProjectListView.this::createProjectCard));
                    setEmptyStateComponent(createEmptyProjectListComponent(canCreate));
                    setSizeFull();
                    addThemeName("no-border");
                    // TODO Workaround until we get bind support into the API
                    addSelectionListener(e -> viewModel.selectedProjectId.value(
                            e.getFirstSelectedItem().map(ProjectListItem::projectId).orElse(null)));
                    ComponentEffect.effect(this, () -> Optional.ofNullable(viewModel.selectedProjectId.value())
                            // TODO This could be simplified if we could select by ID instead of by item. We would not need
                            //  an extra database call just for the selection.
                            .flatMap(projectService::findProjectListItemById)
                            .ifPresentOrElse(this::select, this::deselectAll)
                    );
                }}
        ) {{
            setSizeFull();
            setPadding(false);
            setSpacing(false);
            getStyle().setOverflow(Style.Overflow.HIDDEN);
        }};
    }

    private Component createProjectCard(ProjectListItem projectListItem) {
        return new Card() {{
            setTitle(projectListItem.projectName());
            add(projectListItem.description());
            addToFooter(
                    new Span(projectListItem.tasks() == 1
                            ? "1 task" :
                            "%d tasks".formatted(projectListItem.tasks())) {{
                        getStyle().setColor("var(--vaadin-text-color-secondary)");
                    }},
                    new Span(projectListItem.assignees() == 1
                            ? "1 assignee"
                            : "%d assignees".formatted(projectListItem.assignees())) {{
                        getStyle().setColor("var(--vaadin-text-color-secondary)");
                    }}
            );
        }};
    }

    private Component createEmptyProjectListComponent(boolean canCreate) {
        return new VerticalLayout(
                AppIcon.FOLDER_CHECK_2.create(AppIcon.Size.XL),
                new H4("No projects found"),
                new Span("Change the search criteria or add a project"),
                new HorizontalLayout(
                        new Button("Add Project", VaadinIcon.PLUS.create(), e -> openAddProjectDialog()) {{
                            addThemeName("tertiary");
                            setVisible(canCreate);
                        }},
                        new Button("Show All", AppIcon.FILTER_NONE.create(), e -> viewModel.searchTerm.value("")) {{
                            addThemeName("tertiary");
                        }})
        ) {{
            setSizeFull();
            setAlignItems(FlexComponent.Alignment.CENTER);
            setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        }};
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
