package com.example.application.taskmanagement;

import com.example.application.security.CurrentUser;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("isAuthenticated()")
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUser currentUser;

    TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, CurrentUser currentUser) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.currentUser = currentUser;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<Project> findProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Task createTask(Project project) {
        return new Task(project, currentUser.require().getZoneId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean hasTasks(Project project) {
        return taskRepository.countAllByProject(project) > 0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveTask(Task task) {
        taskRepository.saveAndFlush(task);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<Task> findTasks(Project project, String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return taskRepository.findAllByProject(project, pageable).toList();
        } else {
            return taskRepository.findAllByProjectAndDescriptionContainingIgnoreCase(project, searchTerm, pageable).toList();
        }
    }
}
