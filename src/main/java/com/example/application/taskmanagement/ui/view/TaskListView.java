package com.example.application.taskmanagement.ui.view;

import com.example.application.base.ui.component.Badges;
import com.example.application.base.ui.component.EmptyStateWrapper;
import com.example.application.base.ui.component.Notifications;
import com.example.application.base.ui.component.SectionToolbar;
import com.example.application.security.AppUserInfoLookup;
import com.example.application.security.CurrentUser;
import com.example.application.taskmanagement.domain.Project;
import com.example.application.taskmanagement.domain.Task;
import com.example.application.taskmanagement.service.TaskService;
import com.example.application.taskmanagement.ui.component.AddTaskDialog;
import com.example.application.taskmanagement.ui.component.EditTaskDialog;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Optional;

@Route(value = "projects/:projectId", layout = ProjectListView.class)
@PermitAll // When security is enabled, allow all authenticated users
class TaskListView extends Main implements AfterNavigationObserver, HasDynamicTitle {

    public static final String PARAM_PROJECT_ID = "projectId";

    private final AppUserInfoLookup appUserInfoLookup;
    private final TaskService taskService;
    private final H2 title;
    private final EmptyStateWrapper gridWrapper;
    private final TaskList taskList;
    private final ZoneId timeZone;

    @Nullable
    private Project project;

    TaskListView(CurrentUser currentUser, AppUserInfoLookup appUserInfoLookup, TaskService taskService) {
        this.appUserInfoLookup = appUserInfoLookup;
        this.taskService = taskService;
        this.timeZone = currentUser.require().getZoneId();

        title = new H2("");

        var addTaskButton = new Button("Add Task", event -> addTask());
        addTaskButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        taskList = new TaskList();

        gridWrapper = new EmptyStateWrapper(new NoTasks(), taskList);
        gridWrapper.setSizeFull();

        setSizeFull();
        addClassNames("task-list-view", LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
        add(new SectionToolbar(title, addTaskButton), gridWrapper);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        event.getRouteParameters().getLong(PARAM_PROJECT_ID).flatMap(taskService::findProjectById)
                .ifPresentOrElse(this::setProject, ProjectListView::showProjects);
    }

    private void setProject(Project project) {
        this.project = project;
        refresh();
    }

    @Override
    public String getPageTitle() {
        return "Tasks - " + Optional.ofNullable(project).map(Project::getName).orElse("No Project");
    }

    private class NoTasks extends Div {

        NoTasks() {
            var icon = new SvgIcon("icons/list_alt_check.svg");
            icon.setSize("60px");
            var title = new H4("No tasks yet");
            var instruction = new Span("Add a task to get started");
            var addProject = new Button("Add Task", VaadinIcon.PLUS.create(), event -> addTask());
            addProject.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            setSizeFull();
            addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.AlignItems.CENTER,
                    LumoUtility.JustifyContent.CENTER);
            var centerDiv = new Div(icon, title, instruction, addProject);
            centerDiv.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                    LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
            add(centerDiv);
        }
    }

    private class TaskList extends Div {

        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(getLocale());
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(getLocale());
        private final Grid<Task> grid;

        TaskList() {
            var searchField = new TextField();
            searchField.setPlaceholder("Search");
            searchField.setPrefixComponent(VaadinIcon.SEARCH.create()); // TODO Implement search field!
            searchField.addClassNames(LumoUtility.Flex.GROW);
            searchField.setValueChangeMode(ValueChangeMode.LAZY);
            searchField.addValueChangeListener(event -> refresh());

            // TODO Implement filtering!

            //var viewMode = new Select<String>();
            //viewMode.setItems("Grid View", "Card View"); // TODO Implement view mode!

            grid = new Grid<>();
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.setItemsPageable(pageable -> taskService.findTasks(project, searchField.getValue(), pageable));
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addColumn(new ComponentRenderer<>(this::createStatusBadge)).setHeader("Status").setWidth("150px")
                    .setFlexGrow(0).setSortProperty(Task.STATUS_SORT_PROPERTY);
            grid.addColumn(Task::getDescription).setHeader("Description").setFlexGrow(1)
                    .setSortProperty(Task.DESCRIPTION_SORT_PROPERTY);
            grid.addColumn(new ComponentRenderer<>(this::createDueDate))
                    .setHeader("Due Date (%s)".formatted(timeZone.getDisplayName(TextStyle.SHORT, getLocale())))
                    .setWidth("200px").setFlexGrow(0).setSortProperty(Task.DUE_DATE_SORT_PROPERTY);
            grid.addColumn(new ComponentRenderer<>(this::createPriorityBadge)).setHeader("Priority").setWidth("150px")
                    .setFlexGrow(0).setSortProperty(Task.PRIORITY_SORT_PROPERTY);
            grid.addColumn(new ComponentRenderer<>(this::createAssignees)).setHeader("Assignees");
            grid.addColumn(new ComponentRenderer<>(this::createActionMenu)).setTextAlign(ColumnTextAlign.END)
                    .setWidth("100px").setFlexGrow(0);

            setSizeFull();
            addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
            add(new SectionToolbar(searchField), grid);
        }

        private Component createStatusBadge(Task task) {
            return switch (task.getStatus()) {
            case PENDING -> Badges.createContrast(task.getStatus().getDisplayName());
            case IN_PROGRESS -> Badges.createDefault(task.getStatus().getDisplayName());
            case PAUSED -> Badges.createError(task.getStatus().getDisplayName());
            case DONE -> Badges.createSuccess(task.getStatus().getDisplayName());
            };
        }

        private Component createPriorityBadge(Task task) {
            var badge = new Span(task.getPriority().getDisplayName());
            var themeList = badge.getElement().getThemeList();
            themeList.add("badge");
            switch (task.getPriority()) {
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
            var dueDateTime = task.getDueDateTimeInZone(timeZone);
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
            if (task.getAssignees().isEmpty()) {
                return Badges.createContrast("None");
            }

            var assignees = new AvatarGroup();
            task.getAssignees().stream().flatMap(userId -> appUserInfoLookup.findUserInfo(userId).stream())
                    .map(userInfo -> new AvatarGroup.AvatarGroupItem(userInfo.getFullName(), userInfo.getPictureUrl()))
                    .forEach(assignees::add);
            return assignees;
        }

        private Component createActionMenu(Task task) {
            var menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE,
                    MenuBarVariant.LUMO_END_ALIGNED);
            var item = menuBar.addItem(new SvgIcon("icons/more_vert.svg"));
            var subMenu = item.getSubMenu();
            subMenu.addItem("Edit", event -> editTask(task));
            var deleteItem = subMenu.addItem("Delete", event -> deleteTask(task));
            deleteItem.addClassNames(LumoUtility.TextColor.ERROR);
            return menuBar;
        }
    }

    private void addTask() {
        if (project == null) {
            throw new IllegalStateException("Cannot add task: project is null");
        }

        var dialog = new AddTaskDialog(appUserInfoLookup, () -> taskService.createTask(project), newTask -> {
            taskService.saveTask(newTask);
            refresh();
            notifyProjectTasksChanged();
            Notifications.createNonCriticalNotification(new SvgIcon("icons/check.svg"), "Task created successfully",
                    NotificationVariant.LUMO_SUCCESS).open();
        });
        dialog.open();
    }

    private void editTask(Task task) {
        var dialog = new EditTaskDialog(appUserInfoLookup, task, editedTask -> {
            refreshTask(taskService.saveTask(editedTask));
            notifyProjectTasksChanged();
            Notifications.createNonCriticalNotification(new SvgIcon("icons/check.svg"), "Task updated successfully",
                    NotificationVariant.LUMO_SUCCESS).open();
        });
        dialog.open();
    }

    private void deleteTask(Task task) {
        var dialog = new ConfirmDialog("Delete Task", "Are you sure you want to delete this task?", "Delete", event -> {
            taskService.deleteTask(task);
            refresh();
            notifyProjectTasksChanged();
            Notifications.createNonCriticalNotification(new SvgIcon("icons/delete_sweep.svg"),
                    "Task deleted successfully", NotificationVariant.LUMO_ERROR).open();
        }, "Cancel", event -> {
        });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }

    private void refresh() {
        if (project == null) {
            throw new IllegalStateException("Cannot refresh: project is null");
        }
        title.setText(project.getName());
        gridWrapper.setEmpty(!taskService.hasTasks(project));
        taskList.grid.getDataProvider().refreshAll();
    }

    private void refreshTask(Task task) {
        taskList.grid.getDataProvider().refreshItem(task);
    }

    private void notifyProjectTasksChanged() {
        if (project == null) {
            throw new IllegalStateException("Cannot notify: project is null");
        }
        var event = new ProjectTasksChangedEvent(this, project.requireId());
        propagateEvent(this, event);
    }

    private static void propagateEvent(Component component, ComponentEvent<? extends Component> event) {
        ComponentUtil.fireEvent(component, event);
        component.getParent().ifPresent(parent -> propagateEvent(parent, event));
    }

    public static void showTasksForProjectId(Long projectId) {
        UI.getCurrent().navigate(TaskListView.class, new RouteParam(PARAM_PROJECT_ID, projectId));
    }
}
