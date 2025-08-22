package com.example.application.taskmanagement.service;

import com.example.application.AppRoles;
import com.example.application.taskmanagement.domain.Project;
import com.example.application.taskmanagement.domain.ProjectRepository;
import com.example.application.taskmanagement.dto.ProjectListItem;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("isAuthenticated()")
public class ProjectService {

    private final ProjectRepository projectRepository;

    ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasProjects() {
        return projectRepository.count() > 0;
    }

    @Transactional(readOnly = true)
    public List<ProjectListItem> findProjectListItems(@Nullable String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return projectRepository.findAllProjectListItems(pageable);
        } else {
            return projectRepository.findProjectListItemsBySearchTerm(searchTerm, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Optional<ProjectListItem> findProjectListItemById(Long projectId) {
        return projectRepository.findProjectListItemById(projectId);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.ADMIN + "')")
    public Project saveProject(Project project) {
        return projectRepository.saveAndFlush(project);
    }
}
