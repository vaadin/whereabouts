package com.example.application.taskmanagement.service;

import com.example.application.security.AppRoles;
import com.example.application.taskmanagement.domain.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("isAuthenticated()")
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Project> findProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Transactional(readOnly = true)
    public boolean hasTasks(Project project) {
        return taskRepository.countAllByProject(project) > 0;
    }

    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.saveAndFlush(task);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.ADMIN + "')")
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public List<Task> findTasks(Project project, @Nullable TaskFilter filter, Pageable pageable) {
        if (filter == null || filter.isEmpty()) {
            return taskRepository.findAll(TaskSpecifications.byProject(project), pageable).toList();
        } else {
            return taskRepository.findAll(TaskSpecifications.byProject(project).and(filter.toSpecification()), pageable)
                    .toList();
        }
    }
}
