package com.example.whereabouts.projects.ui;

import com.example.whereabouts.common.ui.*;
import com.example.whereabouts.humanresources.*;
import com.example.whereabouts.projects.*;
import com.example.whereabouts.security.AppRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Pageable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Route(value = "projects/:projectId", layout = ProjectListView.class)
@RolesAllowed(AppRoles.PROJECT_READ)
class ProjectDetailsView extends VerticalLayout {

    public static final String PARAM_PROJECT_ID = "projectId";

    final static class ViewModel {

        private final TaskService taskService;
        private final Supplier<ProjectListView.ViewModel> parentViewModel;
        final ValueSignal<ZoneId> timeZone = new ValueSignal<>(ZoneId.class);
        final ValueSignal<TaskFilter> filter = new ValueSignal<>(TaskFilter.empty());
        final ValueSignal<Project> project = new ValueSignal<>(Project.class);
        final DataProvider<Task, Void> tasks;

        ViewModel(Component owner, Supplier<ProjectListView.ViewModel> parentViewModel,
                  TaskService taskService) {
            this.taskService = taskService;
            this.parentViewModel = parentViewModel;
            tasks = new CallbackDataProvider<>(
                    query -> {
                        // The data provider requires us to always call these methods, or it will throw an exception.
                        var limit = query.getLimit();
                        var offset = query.getOffset();
                        var projectId = project.peek().id();
                        //noinspection ConstantValue // TODO Can we get rid of this? It is caused by the @NullMarked annotation.
                        if (projectId == null) {
                            return Stream.empty();
                        } else {
                            return taskService.findTasks(projectId, filter.peek(), limit, offset,
                                    SortOrderUtil.toSortOrderList(TaskSortableProperty::valueOf, query.getSortOrders()));
                        }
                    },
                    query -> {
                        throw new UnsupportedOperationException("Count not supported");
                    },
                    Task::id
            );
            owner.addAttachListener(event -> event.getUI().getPage()
                    // TODO Would be awesome if we could access the extendedClientDetails directly as a signal
                    .retrieveExtendedClientDetails(extendedClientDetails ->
                            timeZone.value(
                                    Optional.ofNullable(extendedClientDetails.getTimeZoneId())
                                            .map(ZoneId::of)
                                            .orElse(ZoneId.systemDefault())
                            )));
            ComponentEffect.effect(owner, () -> {
                // TODO Is there a smarter way of getting access to a signal defined higher up in the route chain?
                var projectId = parentViewModel.get().selectedProjectId.value();
                project.value(Optional.ofNullable(projectId).flatMap(taskService::findProjectById).orElse(null));
            });
            ComponentEffect.effect(owner, () -> {
                // TODO This is a workaround until we get better API support.
                filter.value();
                tasks.refreshAll();
            });
        }

        void addTask(TaskData newTaskData) {
            taskService.insertTask(newTaskData);
            tasks.refreshAll();
            parentViewModel.get().projects.refreshAll(); // To update task and assignee count
            // TODO Not sure if it should be the view model that is responsible for showing notifications; should
            //  probably be done with signals in some way ;-).
            Notifications.createNonCriticalNotification(AppIcon.CHECK.create(AppIcon.Size.M, AppIcon.Color.GREEN), "Task created successfully").open();
        }

        void updateTask(Task editedTask) {
            taskService.updateTask(editedTask);
            tasks.refreshAll();
            parentViewModel.get().projects.refreshAll(); // To update task and assignee count
            Notifications.createNonCriticalNotification(AppIcon.CHECK.create(AppIcon.Size.M, AppIcon.Color.GREEN), "Task updated successfully").open();
        }

        void deleteTask(Task taskToDelete) {
            taskService.deleteTask(taskToDelete.id());
            tasks.refreshAll();
            parentViewModel.get().projects.refreshAll(); // To update task and assignee count
            Notifications.createNonCriticalNotification(AppIcon.DELETE_SWEEP.create(AppIcon.Size.M, AppIcon.Color.RED), "Task deleted successfully").open();
        }
    }

    private final ViewModel viewModel;
    private final EmployeeService employeeService;

    ProjectDetailsView(AuthenticationContext authenticationContext,
                       TaskService taskService, EmployeeService employeeService) {
        this.employeeService = employeeService;
        this.viewModel = new ViewModel(this, this::getParentViewModel, taskService);
        var canCreate = authenticationContext.hasRole(AppRoles.TASK_CREATE);
        var canUpdate = authenticationContext.hasRole(AppRoles.TASK_UPDATE);
        var canDelete = authenticationContext.hasRole(AppRoles.TASK_DELETE);

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        ComponentEffect.effect(this, () -> {
            var project = viewModel.project.value();
            var timeZone = viewModel.timeZone.value();
            removeAll();

            if (project != null && timeZone != null) {
                add(
                        new SectionToolbar(
                                new H2(project.data().name()),
                                SectionToolbar.group(
                                        new Button("Add Task", e -> openAddTaskDialog(project, timeZone)) {{
                                            addThemeName("primary");
                                            setVisible(canCreate);
                                        }},
                                        new Button() {{
                                            getElement().appendChild(AppIcon.CLOSE.create().getElement()); // TODO Until we get an icon-only button variant for Aura
                                            addClickListener(e -> getParentViewModel().selectedProjectId.value(null));
                                        }})
                        ) {{
                            getStyle().setBorderBottom("1px solid var(--vaadin-border-color-secondary)");
                        }},
                        createTaskListComponent(project, timeZone, canCreate, canUpdate, canDelete)
                );
                UI.getCurrent().getPage().setTitle("Project Tasks - " + project.data().name());
            }
        });
    }

    private List<EmployeeReference> findAssignees(Pageable pageable, @Nullable String searchTerm) {
        return employeeService.findReferencesByFilter(pageable, new EmployeeFilter(searchTerm, Set.of(EmploymentStatus.ACTIVE), Set.of()));
    }

    private void openAddTaskDialog(Project project, ZoneId timeZone) {
        new AddTaskDialog(this::findAssignees, employeeService::findReferencesByIds, TaskData.createDefault(project.id(), timeZone), viewModel::addTask).open();
    }

    private void openEditTaskDialog(Task task) {
        new EditTaskDialog(this::findAssignees, employeeService::findReferencesByIds, task, viewModel::updateTask).open();
    }

    private void openDeleteTaskDialog(Task task) {
        new ConfirmDialog(
                "Delete Task", "Are you sure you want to delete this task?",
                "Delete", e -> viewModel.deleteTask(task),
                "Cancel", event -> { /* NOOP */ }) {{
            setConfirmButtonTheme("error primary");
        }}.open();
    }

    private ProjectListView.ViewModel getParentViewModel() {
        return getParent().filter(ProjectListView.class::isInstance).map(ProjectListView.class::cast).orElseThrow().viewModel;
    }

    private Component createTaskListComponent(Project project, ZoneId timeZone, boolean canCreate, boolean canUpdate, boolean canDelete) {
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());
        var timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(getLocale());
        // TODO I'm not sure what I feel about this style of creating views
        return new VerticalLayout() {
            {
                add(
                        new SectionToolbar(
                                new TextField() {{
                                    setPlaceholder("Search");
                                    setPrefixComponent(AppIcon.SEARCH.create());
                                    setValueChangeMode(ValueChangeMode.LAZY);
                                    // TODO Workaround until we get bind support into the API:
                                    addValueChangeListener(e -> viewModel.filter.update(old -> old.withSearchTerm(e.getValue())));
                                    ComponentEffect.effect(this, () -> setValue(viewModel.filter.value().searchTerm()));
                                }},
                                createFilterMenu()) {{
                            getStyle().setBorderBottom("1px solid var(--vaadin-border-color-secondary)");
                        }},
                        new Grid<Task>() {{
                            setSelectionMode(Grid.SelectionMode.NONE);
                            setDataProvider(viewModel.tasks);
                            getLazyDataView().setItemCountUnknown(); // Because we don't provide a count callback
                            addColumn(new ComponentRenderer<>(task -> createStatusBadge(task))).setHeader("Status").setWidth("150px")
                                    .setFlexGrow(0).setSortProperty(TaskSortableProperty.STATUS.name());
                            addColumn(task -> task.data().description()).setHeader("Description").setFlexGrow(1)
                                    .setSortProperty(TaskSortableProperty.DESCRIPTION.name());
                            addColumn(new ComponentRenderer<>(task -> createDueDate(task)))
                                    .setHeader("Due Date (%s)".formatted(timeZone.getDisplayName(TextStyle.SHORT, getLocale())))
                                    .setWidth("200px").setFlexGrow(0).setSortProperty(TaskSortableProperty.DUE_DATE.name());
                            addColumn(new ComponentRenderer<>(task -> createPriorityBadge(task))).setHeader("Priority").setWidth("150px")
                                    .setFlexGrow(0).setSortProperty(TaskSortableProperty.PRIORITY.name());
                            addColumn(new ComponentRenderer<>(task -> createAssignees(task))).setHeader("Assignees");
                            addColumn(new ComponentRenderer<>(task -> createActionMenu(task))).setTextAlign(ColumnTextAlign.END)
                                    .setWidth("60px").setFlexGrow(0);
                            var cardColumn = addColumn(new ComponentRenderer<>(task -> createTaskCard(task)));
                            setEmptyStateComponent(createEmptyComponent(canCreate, project, timeZone));
                            setSizeFull();
                            createContextMenu(addContextMenu());
                            addThemeName("no-border");
                            // TODO We should get a product API for achieving something similar:
                            var resizeObserver = new ResizeObserver(this);
                            resizeObserver.addListener(e -> {
                                boolean showCardView = e.width() < 800;
                                cardColumn.setVisible(showCardView);
                                getColumns().stream().filter(c -> c != cardColumn).forEach(c -> c.setVisible(!showCardView));
                                if (showCardView) {
                                    addClassName("card-view");
                                    addThemeName("no-row-borders");
                                } else {
                                    removeClassName("card-view");
                                    removeThemeName("no-row-borders");
                                }
                            });
                        }}
                );
                setSizeFull();
                setPadding(false);
                setSpacing(false);
            }

            private Component createStatusBadge(Task task) {
                var displayName = TaskStatusFormatter.ofLocale(getLocale()).getDisplayName(task.data().status());
                return switch (task.data().status()) {
                    case PENDING -> Badges.create(displayName);
                    case PLANNED, IN_PROGRESS -> Badges.createBlue(displayName);
                    case PAUSED -> Badges.createRed(displayName);
                    case DONE -> Badges.createGreen(displayName);
                };
            }

            private Component createPriorityBadge(Task task) {
                var displayName = TaskPriorityFormatter.ofLocale(getLocale()).getDisplayName(task.data().priority());
                return switch (task.data().priority()) {
                    case URGENT -> Badges.createRed(displayName);
                    case HIGH -> Badges.createYellow(displayName);
                    case NORMAL -> Badges.createBlue(displayName);
                    case LOW -> Badges.createGreen(displayName);
                };
            }

            private Component createDueDate(Task task) {
                var dueDateTime = task.data().dueDateTimeInZone(timeZone);
                if (dueDateTime == null) {
                    return Badges.create("Never");
                }

                return new Div(
                        new Div(dateFormatter.format(dueDateTime)),
                        new Div(timeFormatter.format(dueDateTime)) {{
                            getStyle().setColor("var(--vaadin-text-color-secondary)");
                        }}
                );
            }

            private Component createAssignees(Task task) {
                if (task.data().assignees().isEmpty()) {
                    return Badges.create("None");
                }

                var assignees = new AvatarGroup();
                var nameFormatter = PersonNameFormatter.firstLast();
                employeeService.findReferencesByIds(task.data().assignees()).stream()
                        .map(assignee -> new AvatarGroup.AvatarGroupItem(nameFormatter.toFullName(assignee)))
                        .forEach(assignees::add);
                return assignees;
            }

            private Component createActionMenu(Task task) {
                var menuBar = new MenuBar();
                menuBar.addThemeName("icon");
                var item = menuBar.addItem(AppIcon.MORE_VERT.create());
                var subMenu = item.getSubMenu();
                if (canUpdate) {
                    subMenu.addItem("Edit", e -> openEditTaskDialog(task));
                }
                if (canDelete) {
                    var deleteItem = subMenu.addItem("Delete", e -> openDeleteTaskDialog(task));
                    deleteItem.getStyle().setColor("var(--aura-red)");
                }
                return menuBar;
            }

            private Component createTaskCard(Task task) {
                return new Card() {{
                    setHeader(new HorizontalLayout(createStatusBadge(task), createPriorityBadge(task)));
                    setHeaderSuffix(createActionMenu(task));
                    add(task.data().description());
                    addThemeName("outlined");
                    var dueDateTime = task.data().dueDateTimeInZone(timeZone);
                    if (dueDateTime != null) {
                        add(
                                new Div("Due on %s at %s".formatted(dateFormatter.format(dueDateTime), timeFormatter.format(dueDateTime))) {{
                                    getStyle().setColor("var(--vaadin-text-color-secondary)");
                                    getStyle().setPaddingTop("var(--vaadin-gap-m)");
                                }});
                    }
                    addToFooter(createAssignees(task));
                }};
            }

            private void createContextMenu(GridContextMenu<Task> contextMenu) {
                if (canUpdate) {
                    contextMenu.addItem("Edit", e -> e.getItem()
                            .ifPresent(task -> openEditTaskDialog(task)));
                }
                if (canDelete) {
                    var deleteItem = contextMenu.addItem("Delete", e -> e.getItem()
                            .ifPresent(task -> openDeleteTaskDialog(task)));
                    deleteItem.getStyle().setColor("var(--aura-red)");
                }
                // Don't show the menu unless opened on a row
                contextMenu.setDynamicContentHandler(Objects::nonNull);
            }

            private Component createFilterMenu() {
                var menuBar = new MenuBar();
                var item = menuBar.addItem(AppIcon.FILTER_LIST.create(), "Filters");
                var subMenu = item.getSubMenu();

                var statusFormatter = TaskStatusFormatter.ofLocale(getLocale());
                for (var status : TaskStatus.values()) {
                    subMenu.addItem(statusFormatter.getDisplayName(status), e -> {
                        if (e.getSource().isChecked()) {
                            viewModel.filter.update(old -> old.withStatus(status));
                        } else {
                            viewModel.filter.update(old -> old.withoutStatus(status));
                        }
                    }).setCheckable(true);
                }
                subMenu.addSeparator();
                var priorityFormatter = TaskPriorityFormatter.ofLocale(getLocale());
                for (var priority : TaskPriority.values()) {
                    subMenu.addItem(priorityFormatter.getDisplayName(priority), e -> {
                        if (e.getSource().isChecked()) {
                            viewModel.filter.update(old -> old.withPriority(priority));
                        } else {
                            viewModel.filter.update(old -> old.withoutPriority(priority));
                        }
                    }).setCheckable(true);
                }
                return menuBar;
            }
        };
    }

    private Component createEmptyComponent(boolean canCreate, Project project, ZoneId timeZone) {
        return new VerticalLayout(
                AppIcon.LIST_ALT_CHECK.create(AppIcon.Size.XL),
                new H4("No tasks found"),
                new Span("Change the search criteria or add a task"),
                new Button("Add Task", VaadinIcon.PLUS.create(), e -> openAddTaskDialog(project, timeZone)) {{
                    addThemeName("tertiary");
                    setVisible(canCreate);
                }}
        ) {{
            setSizeFull();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
        }};
    }
}
