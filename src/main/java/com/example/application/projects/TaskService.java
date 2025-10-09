package com.example.application.projects;

import com.example.application.projects.internal.ProjectRepository;
import com.example.application.projects.internal.TaskRepository;
import com.example.application.security.AppRoles;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasRole('" + AppRoles.TASK_READ + "')")
@NullMarked
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Project> findProjectById(ProjectId projectId) {
        return projectRepository.findById(projectId);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.TASK_CREATE + "')")
    public TaskId insertTask(TaskData data) {
        return taskRepository.insert(data);
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
    public List<Task> findTasks(ProjectId project, @Nullable TaskFilter filter, Pageable pageable) {
        if (filter == null || filter.isEmpty()) {
            return taskRepository.findAll(project, pageable);
        } else {
            return taskRepository.findByFilter(project, filter, pageable);
        }
    }
}
