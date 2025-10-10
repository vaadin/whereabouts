package com.example.application.projects;

import com.example.application.projects.internal.ProjectRepository;
import com.example.application.projects.internal.TaskQuery;
import com.example.application.projects.internal.TaskRepository;
import com.example.application.security.AppRoles;
import com.vaadin.flow.data.provider.SortOrder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@PreAuthorize("hasRole('" + AppRoles.PROJECT_READ + "')")
@NullMarked
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskQuery taskQuery;

    TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, TaskQuery taskQuery) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.taskQuery = taskQuery;
    }

    @Transactional(readOnly = true)
    public Optional<Project> findProjectById(ProjectId projectId) {
        return projectRepository.findById(projectId);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.TASK_CREATE + "')")
    public void insertTask(TaskData data) {
        taskRepository.insert(data);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.TASK_UPDATE + "')")
    public Task updateTask(Task task) {
        return taskRepository.update(task);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.TASK_DELETE + "')")
    public void deleteTask(TaskId id) {
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Stream<Task> findTasks(ProjectId project, TaskFilter filter, int limit, int offset, List<SortOrder<TaskSortableProperty>> sortOrders) {
        return taskRepository.findByFilter(project, filter, limit, offset, sortOrders);
    }

    @Transactional(readOnly = true)
    public Stream<TaskAssignee> findAssigneesBySearchTerm(@Nullable String searchTerm, int limit, int offset) {
        return taskQuery.findAssigneesBySearchTerm(searchTerm, limit, offset);
    }
}
