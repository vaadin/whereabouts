package com.example.application.taskmanagement.service;

import com.example.application.security.CurrentUser;
import com.example.application.taskmanagement.domain.Project;
import com.example.application.taskmanagement.domain.ProjectRepository;
import com.example.application.taskmanagement.domain.Task;
import com.example.application.taskmanagement.domain.TaskRepository;
import com.example.application.taskmanagement.domain.event.TaskCreatedEvent;
import com.example.application.taskmanagement.domain.event.TaskDeletedEvent;
import com.example.application.taskmanagement.domain.event.TaskUpdatedEvent;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
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
    private final CurrentUser currentUser;
    private final ApplicationEventPublisher applicationEventPublisher;

    TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, CurrentUser currentUser,
                ApplicationEventPublisher applicationEventPublisher) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.currentUser = currentUser;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(readOnly = true)
    public Optional<Project> findProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    public Task createTask(Project project) {
        return new Task(project, currentUser.require().getZoneId());
    }

    @Transactional(readOnly = true)
    public boolean hasTasks(Project project) {
        return taskRepository.countAllByProject(project) > 0;
    }

    @Transactional
    public void saveTask(Task task) {
        var isNew = task.getId() == null;
        var saved = taskRepository.saveAndFlush(task);
        if (isNew) {
            applicationEventPublisher.publishEvent(new TaskCreatedEvent(saved));
        } else {
            applicationEventPublisher.publishEvent(new TaskUpdatedEvent(saved));
        }
    }

    @Transactional
    public void deleteTask(Task task) {
        taskRepository.delete(task);
        applicationEventPublisher.publishEvent(new TaskDeletedEvent(task));
    }

    @Transactional(readOnly = true)
    public List<Task> findTasks(Project project, @Nullable String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return taskRepository.findAllByProject(project, pageable).toList();
        } else {
            return taskRepository.findAllByProjectAndDescriptionContainingIgnoreCase(project, searchTerm, pageable).toList();
        }
    }
}
