package com.example.application.projects.ui;

import com.example.application.base.ui.component.Badges;
import com.example.application.base.ui.component.ResizeObserver;
import com.example.application.common.ui.Notifications;
import com.example.application.common.ui.SectionToolbar;
import com.example.application.projects.*;
import com.example.application.security.AppRoles;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentUtil;
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
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.PermitAll;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Route(value = "projects/:projectId", layout = ProjectListView.class)
@PermitAll // When security is enabled, allow all authenticated users
class ProjectDetailsView extends Main implements AfterNavigationObserver, HasDynamicTitle {

    public static final String PARAM_PROJECT_ID = "projectId";

    private final TaskService taskService;
    private final H2 title;
    private final TaskList taskList;
    private final boolean isAdmin;

    private final ValueSignal<ZoneId> timeZoneSignal = new ValueSignal<>(ZoneId.class);
    private final ValueSignal<Project> projectSignal = new ValueSignal<>(Project.class);
    private ZoneId timeZone = ZoneId.systemDefault(); // TODO Replace with timezone from browser, stored in a signal

    @Nullable
    private Project project; // TODO Replace with a signal.

    ProjectDetailsView(AuthenticationContext authenticationContext,
                       TaskService taskService) {
        isAdmin = authenticationContext.hasRole(AppRoles.ADMIN);

        this.taskService = taskService;

        title = new H2("");

        var addTaskButton = new Button("Add Task", event -> addTask());
        addTaskButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        taskList = new TaskList();

        setSizeFull();
        addClassNames("task-list-view", LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
        add(new SectionToolbar(title, addTaskButton), taskList);
    }


    private void setProject(Project projectSignal) {
        this.projectSignal = projectSignal;
        refresh();
    }


    private void addTask() {
        if (projectSignal == null) {
            throw new IllegalStateException("Cannot add task: project is null");
        }

        var dialog = new AddTaskDialog(appUserLookupService, () -> new Task(projectSignal, timeZoneSignal), newTask -> {
            taskService.saveTask(newTask);
            refresh();
            notifyProjectTasksChanged();
            Notifications.createNonCriticalNotification(new SvgIcon("icons/check.svg"), "Task created successfully",
                    NotificationVariant.LUMO_SUCCESS).open();
        });
        dialog.open();
    }

    private void editTask(Task task) {
        var dialog = new EditTaskDialog(appUserLookupService, task, editedTask -> {
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
        if (projectSignal == null) {
            throw new IllegalStateException("Cannot refresh: project is null");
        }
        title.setText(projectSignal.getName());
        taskList.grid.getDataProvider().refreshAll();
    }

    private void refreshTask(Task task) {
        taskList.grid.getDataProvider().refreshItem(task);
    }

    private void notifyProjectTasksChanged() {
        if (projectSignal == null) {
            throw new IllegalStateException("Cannot notify: project is null");
        }
        var event = new ProjectTasksChangedEvent(this, projectSignal.requireId());
        propagateEvent(this, event);
    }

    private static void propagateEvent(Component component, ComponentEvent<? extends Component> event) {
        ComponentUtil.fireEvent(component, event);
        component.getParent().ifPresent(parent -> propagateEvent(parent, event));
    }

    private class TaskList extends Div {

        private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(getLocale());
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(getLocale());
        private final Grid<Task> grid;
        private final TaskFilter filter;
        private final Grid.Column<Task> cardColumn;

        TaskList() {
            filter = new TaskFilter();

            var searchField = new TextField();
            searchField.setPlaceholder("Search");
            searchField.setPrefixComponent(new SvgIcon("icons/search.svg"));
            searchField.addClassNames(LumoUtility.Flex.GROW);
            searchField.setValueChangeMode(ValueChangeMode.LAZY);
            searchField.addValueChangeListener(event -> {
                filter.setSearchTerm(event.getValue());
                refresh();
            });

            var filterMenu = createFilterMenu();

            grid = new Grid<>();
            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.setItemsPageable(pageable -> projectSignal == null ? Collections.emptyList() : taskService.findTasks(projectSignal, filter, pageable));
            grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            grid.addColumn(new ComponentRenderer<>(this::createStatusBadge)).setHeader("Status").setWidth("150px")
                    .setFlexGrow(0).setSortProperty(Task.STATUS_SORT_PROPERTY);
            grid.addColumn(Task::getDescription).setHeader("Description").setFlexGrow(1)
                    .setSortProperty(Task.DESCRIPTION_SORT_PROPERTY);
            grid.addColumn(new ComponentRenderer<>(this::createDueDate))
                    .setHeader("Due Date (%s)".formatted(timeZoneSignal.getDisplayName(TextStyle.SHORT, getLocale())))
                    .setWidth("200px").setFlexGrow(0).setSortProperty(Task.DUE_DATE_SORT_PROPERTY);
            grid.addColumn(new ComponentRenderer<>(this::createPriorityBadge)).setHeader("Priority").setWidth("150px")
                    .setFlexGrow(0).setSortProperty(Task.PRIORITY_SORT_PROPERTY);
            grid.addColumn(new ComponentRenderer<>(this::createAssignees)).setHeader("Assignees");
            grid.addColumn(new ComponentRenderer<>(this::createActionMenu)).setTextAlign(ColumnTextAlign.END)
                    .setWidth("60px").setFlexGrow(0);
            cardColumn = grid.addColumn(new ComponentRenderer<>(this::createTaskCard));
            grid.setEmptyStateComponent(new TaskListEmptyComponent());
            createContextMenu(grid.addContextMenu());

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
            return switch (task.getStatus()) {
                case PENDING -> Badges.createContrast(task.getStatus().getDisplayName());
                case PLANNED, IN_PROGRESS -> Badges.createDefault(task.getStatus().getDisplayName());
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
            var dueDateTime = task.getDueDateTimeInZone(timeZoneSignal);
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
            task.getAssignees().stream()
                    .map(userInfo -> new AvatarGroup.AvatarGroupItem(userInfo.getDisplayName()))
                    .forEach(assignees::add);
            return assignees;
        }

        private Component createActionMenu(Task task) {
            var menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE,
                    MenuBarVariant.LUMO_END_ALIGNED, MenuBarVariant.LUMO_SMALL);
            var item = menuBar.addItem(new SvgIcon("icons/more_vert.svg"));
            var subMenu = item.getSubMenu();
            subMenu.addItem("Edit", event -> editTask(task));
            if (isAdmin) {
                var deleteItem = subMenu.addItem("Delete", event -> deleteTask(task));
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
            card.add(task.getDescription());
            var dueDateTime = task.getDueDateTimeInZone(timeZoneSignal);
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
            contextMenu.addItem("Edit", event -> event.getItem().ifPresent(ProjectDetailsView.this::editTask));
            if (isAdmin) {
                var deleteItem = contextMenu.addItem("Delete",
                        event -> event.getItem().ifPresent(ProjectDetailsView.this::deleteTask));
                deleteItem.addClassName(LumoUtility.TextColor.ERROR);
            }
            // Don't show the menu unless opened on a row
            contextMenu.setDynamicContentHandler(Objects::nonNull);
        }

        private Component createFilterMenu() {
            var menuBar = new MenuBar();
            menuBar.addThemeVariants(MenuBarVariant.LUMO_DROPDOWN_INDICATORS);
            var item = menuBar.addItem(new SvgIcon("icons/filter_list.svg"), "Filters");
            var subMenu = item.getSubMenu();

            for (var status : TaskStatus.values()) {
                subMenu.addItem(status.getDisplayName(), event -> {
                    if (event.getSource().isChecked()) {
                        filter.include(status);
                    } else {
                        filter.exclude(status);
                    }
                    refresh();
                }).setCheckable(true);
            }
            subMenu.addSeparator();
            for (var priority : TaskPriority.values()) {
                subMenu.addItem(priority.getDisplayName(), event -> {
                    if (event.getSource().isChecked()) {
                        filter.include(priority);
                    } else {
                        filter.exclude(priority);
                    }
                    refresh();
                }).setCheckable(true);
            }
            return menuBar;
        }
    }

    private class TaskListEmptyComponent extends VerticalLayout {
        TaskListEmptyComponent() {
            var icon = new SvgIcon("icons/list_alt_check.svg");
            icon.setSize("60px");
            var title = new H4("No tasks found");
            var instruction = new Span("Change the search criteria or add a task");

            var addTask = new Button("Add Task", VaadinIcon.PLUS.create(), event -> addTask());
            addTask.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            // TODO Add "Clear search criteria" button

            add(icon, title, instruction, addTask);

            setSizeFull();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
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
