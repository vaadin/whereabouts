package com.example.application.projects.ui;

import com.example.application.common.ui.*;
import com.example.application.projects.*;
import com.example.application.security.AppRoles;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.RolesAllowed;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Objects;
import java.util.Optional;

@Route(value = "projects/:projectId", layout = ProjectListView.class)
@RolesAllowed(AppRoles.PROJECT_READ)
class ProjectDetailsView extends VerticalLayout implements AfterNavigationObserver, HasDynamicTitle {

    public static final String PARAM_PROJECT_ID = "projectId";

    private final TaskService taskService;
    private final boolean canUpdate;
    private final boolean canDelete;

    private final ValueSignal<ZoneId> timeZoneSignal = new ValueSignal<>(ZoneId.class);
    private final ValueSignal<Project> projectSignal = new ValueSignal<>(Project.class);

    ProjectDetailsView(AuthenticationContext authenticationContext,
                       TaskService taskService) {
        this.taskService = taskService;
        var canCreate = authenticationContext.hasRole(AppRoles.TASK_CREATE);
        canUpdate = authenticationContext.hasRole(AppRoles.TASK_UPDATE);
        canDelete = authenticationContext.hasRole(AppRoles.TASK_DELETE);

        ComponentEffect.effect(this, () -> {
            var project = projectSignal.value();
            var timeZone = timeZoneSignal.value();
            removeAll();

            if (project != null && timeZone != null) {
                // Create components
                var title = new H2(project.data().name());

                var addTaskButton = new Button("Add Task");
                addTaskButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                addTaskButton.setVisible(canCreate);

                var taskList = new TaskList(project, timeZone);

                // Add listeners
                addTaskButton.addClickListener(e -> addTask(project, timeZone, taskList.grid.getDataProvider()::refreshAll));

                // Layout components
                setSizeFull();
                add(new SectionToolbar(title, addTaskButton), taskList);
            }
        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI.getCurrent().getPage().retrieveExtendedClientDetails(extendedClientDetails ->
                timeZoneSignal.value(
                        Optional.ofNullable(extendedClientDetails.getTimeZoneId())
                                .map(ZoneId::of)
                                .orElse(ZoneId.systemDefault())
                ));
    }

    private void addTask(Project project, ZoneId timeZone, SerializableRunnable refresh) {
        var dialog = new AddTaskDialog(taskService::findAssigneesBySearchTerm, TaskData.createDefault(project.id(), timeZone), newTaskData -> {
            taskService.insertTask(newTaskData);
            refresh.run();
            getProjectListView().ifPresent(view -> view.onProjectUpdated(newTaskData.project()));
            Notifications.createNonCriticalNotification(AppIcon.CHECK.create(), "Task created successfully",
                    NotificationVariant.LUMO_SUCCESS).open();
        });
        dialog.open();
    }

    private void editTask(Task task, SerializableRunnable refresh) {
        var dialog = new EditTaskDialog(taskService::findAssigneesBySearchTerm, task, editedTask -> {
            var saved = taskService.updateTask(editedTask);
            refresh.run();
            getProjectListView().ifPresent(view -> view.onProjectUpdated(saved.data().project()));
            Notifications.createNonCriticalNotification(AppIcon.CHECK.create(), "Task updated successfully",
                    NotificationVariant.LUMO_SUCCESS).open();
        });
        dialog.open();
    }

    private void deleteTask(Task task, SerializableRunnable refresh) {
        var dialog = new ConfirmDialog("Delete Task", "Are you sure you want to delete this task?", "Delete", event -> {
            taskService.deleteTask(task.id());
            refresh.run();
            getProjectListView().ifPresent(view -> view.onProjectUpdated(task.data().project()));
            Notifications.createNonCriticalNotification(AppIcon.DELETE_SWEEP.create(),
                    "Task deleted successfully", NotificationVariant.LUMO_ERROR).open();
        }, "Cancel", event -> {
        });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private Optional<ProjectListView> getProjectListView() {
        return getParent().filter(ProjectListView.class::isInstance).map(ProjectListView.class::cast);
    }

    private class TaskList extends Div {

        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(getLocale());
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(getLocale());
        private final Project project;
        private final ZoneId timeZone;
        private final Grid<Task> grid;
        private final Grid.Column<Task> cardColumn;
        private final ValueSignal<TaskFilter> filterSignal = new ValueSignal<>(TaskFilter.empty());

        TaskList(Project project, ZoneId timeZone) {
            this.project = project;
            this.timeZone = timeZone;

            var searchField = new TextField();
            searchField.setPlaceholder("Search");
            searchField.setPrefixComponent(AppIcon.SEARCH.create());
            searchField.addClassNames(LumoUtility.Flex.GROW);
            searchField.setValueChangeMode(ValueChangeMode.LAZY);

            var filterMenu = createFilterMenu();

            grid = new Grid<>();
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.setItems(query -> taskService.findTasks(project.id(), filterSignal.peek(),
                    query.getLimit(), query.getOffset(),
                    SortOrderUtil.toSortOrderList(TaskSortableProperty::valueOf, query.getSortOrders())));
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addColumn(new ComponentRenderer<>(this::createStatusBadge)).setHeader("Status").setWidth("150px")
                    .setFlexGrow(0).setSortProperty(TaskSortableProperty.STATUS.name());
            grid.addColumn(task -> task.data().description()).setHeader("Description").setFlexGrow(1)
                    .setSortProperty(TaskSortableProperty.DESCRIPTION.name());
            grid.addColumn(new ComponentRenderer<>(this::createDueDate))
                    .setHeader("Due Date (%s)".formatted(timeZone.getDisplayName(TextStyle.SHORT, getLocale())))
                    .setWidth("200px").setFlexGrow(0).setSortProperty(TaskSortableProperty.DUE_DATE.name());
            grid.addColumn(new ComponentRenderer<>(this::createPriorityBadge)).setHeader("Priority").setWidth("150px")
                    .setFlexGrow(0).setSortProperty(TaskSortableProperty.PRIORITY.name());
            grid.addColumn(new ComponentRenderer<>(this::createAssignees)).setHeader("Assignees");
            grid.addColumn(new ComponentRenderer<>(this::createActionMenu)).setTextAlign(ColumnTextAlign.END)
                    .setWidth("60px").setFlexGrow(0);
            cardColumn = grid.addColumn(new ComponentRenderer<>(this::createTaskCard));
            grid.setEmptyStateComponent(createEmptyComponent());
            createContextMenu(grid.addContextMenu());

            // Add listeners and effects
            searchField.addValueChangeListener(event ->
                    filterSignal.update(old -> old.withSearchTerm(event.getValue())));

            ComponentEffect.effect(this, () -> {
                // Refresh the grid whenever the filter changes
                filterSignal.value();
                grid.getDataProvider().refreshAll();
            });

            // Layout components
            setSizeFull();
            addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
            add(new SectionToolbar(searchField, filterMenu), grid);

            var resizeObserver = new ResizeObserver(this);
            resizeObserver.addListener(this::adjustGridOnResize);
        }

        private void adjustGridOnResize(ResizeObserver.ResizeEvent resizeEvent) {
            boolean showCardView = resizeEvent.width() < 800;
            cardColumn.setVisible(showCardView);
            grid.getColumns().stream().filter(c -> c != cardColumn).forEach(c -> c.setVisible(!showCardView));
            if (showCardView) {
                grid.addClassName("card-view");
                grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
            } else {
                grid.removeClassName("card-view");
                grid.removeThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
            }
        }

        private Component createStatusBadge(Task task) {
            var displayName = task.data().status().getDisplayName();
            return switch (task.data().status()) {
                case PENDING -> Badges.createContrast(displayName);
                case PLANNED, IN_PROGRESS -> Badges.createDefault(displayName);
                case PAUSED -> Badges.createError(displayName);
                case DONE -> Badges.createSuccess(displayName);
            };
        }

        private Component createPriorityBadge(Task task) {
            var badge = new Span(task.data().priority().getDisplayName());
            var themeList = badge.getElement().getThemeList();
            themeList.add("badge");
            switch (task.data().priority()) {
                case URGENT -> {
                    themeList.add("error");
                }
                case HIGH -> {
                    themeList.add("warning");
                }
                case NORMAL -> {
                    // Default style
                }
                case LOW -> {
                    themeList.add("success");
                }
            }
            return badge;
        }

        private Component createDueDate(Task task) {
            var dueDateTime = task.data().dueDateTimeInZone(timeZone);
            if (dueDateTime == null) {
                return Badges.createContrast("Never");
            }

            var dateDiv = new Div();
            var date = new Div(dateFormatter.format(dueDateTime));
            var time = new Div(timeFormatter.format(dueDateTime));
            time.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);
            dateDiv.add(date, time);
            return dateDiv;
        }

        private Component createAssignees(Task task) {
            if (task.data().assignees().isEmpty()) {
                return Badges.createContrast("None");
            }

            var assignees = new AvatarGroup();
            task.data().assignees().stream()
                    .map(assignee -> new AvatarGroup.AvatarGroupItem(assignee.displayName()))
                    .forEach(assignees::add);
            return assignees;
        }

        private Component createActionMenu(Task task) {
            var menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE,
                    MenuBarVariant.LUMO_END_ALIGNED, MenuBarVariant.LUMO_SMALL);
            var item = menuBar.addItem(AppIcon.MORE_VERT.create());
            var subMenu = item.getSubMenu();
            if (canUpdate) {
                subMenu.addItem("Edit", event -> editTask(task, grid.getDataProvider()::refreshAll));
            }
            if (canDelete) {
                var deleteItem = subMenu.addItem("Delete", event -> deleteTask(task, grid.getDataProvider()::refreshAll));
                deleteItem.addClassNames(LumoUtility.TextColor.ERROR);
            }
            return menuBar;
        }

        private Component createTaskCard(Task task) {
            var card = new Card();
            card.addThemeVariants(CardVariant.LUMO_OUTLINED);

            var header = new Div(createStatusBadge(task), createPriorityBadge(task));
            header.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.Gap.SMALL);
            card.setHeader(header);
            card.setHeaderSuffix(createActionMenu(task));
            card.add(task.data().description());
            var dueDateTime = task.data().dueDateTimeInZone(timeZone);
            if (dueDateTime != null) {
                var dueDiv = new Div();
                dueDiv.setText("Due on %s at %s".formatted(dateFormatter.format(dueDateTime), timeFormatter.format(dueDateTime)));
                dueDiv.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL, LumoUtility.Padding.Top.MEDIUM);
                card.add(dueDiv);
            }
            card.addToFooter(createAssignees(task));
            return card;
        }

        private void createContextMenu(GridContextMenu<Task> contextMenu) {
            if (canUpdate) {
                contextMenu.addItem("Edit", event -> event.getItem()
                        .ifPresent(task -> editTask(task, grid.getDataProvider()::refreshAll)));
            }
            if (canDelete) {
                contextMenu.addItem("Delete", event -> event.getItem()
                        .ifPresent(task -> deleteTask(task, grid.getDataProvider()::refreshAll)));
            }
            // Don't show the menu unless opened on a row
            contextMenu.setDynamicContentHandler(Objects::nonNull);
        }

        private Component createFilterMenu() {
            var menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_DROPDOWN_INDICATORS);
            var item = menuBar.addItem(AppIcon.FILTER_LIST.create(), "Filters");
            var subMenu = item.getSubMenu();

            for (var status : TaskStatus.values()) {
                subMenu.addItem(status.getDisplayName(), event -> {
                    if (event.getSource().isChecked()) {
                        filterSignal.update(old -> old.withStatus(status));
                    } else {
                        filterSignal.update(old -> old.withoutStatus(status));
                    }
                }).setCheckable(true);
            }
            subMenu.addSeparator();
            for (var priority : TaskPriority.values()) {
                subMenu.addItem(priority.getDisplayName(), event -> {
                    if (event.getSource().isChecked()) {
                        filterSignal.update(old -> old.withPriority(priority));
                    } else {
                        filterSignal.update(old -> old.withoutPriority(priority));
                    }
                }).setCheckable(true);
            }
            return menuBar;
        }

        private Component createEmptyComponent() {
            var icon = AppIcon.LIST_ALT_CHECK.create();
            icon.setSize("60px");
            var title = new H4("No tasks found");
            var instruction = new Span("Change the search criteria or add a task");

            var addTask = new Button("Add Task", VaadinIcon.PLUS.create(), event -> addTask(project, timeZone, grid.getDataProvider()::refreshAll));
            addTask.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            // TODO Add "Clear search criteria" button

            var layout = new VerticalLayout();
            layout.add(icon, title, instruction, addTask);
            layout.setSizeFull();
            layout.setAlignItems(Alignment.CENTER);
            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            return layout;
        }
    }

    @Override
    public String getPageTitle() {
        return "Project Tasks - " + Optional.ofNullable(projectSignal.value()).map(project -> project.data().name()).orElse("");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        event.getRouteParameters()
                .getLong(PARAM_PROJECT_ID)
                .map(ProjectId::of)
                .flatMap(taskService::findProjectById)
                .ifPresentOrElse(projectSignal::value, ProjectsNavigation::navigateToProjectList);
    }
}
