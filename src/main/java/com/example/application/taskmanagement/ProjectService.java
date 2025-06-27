package com.example.application.taskmanagement;

import com.example.application.taskmanagement.dto.ProjectFormDataObject;
import com.example.application.taskmanagement.dto.ProjectListItem;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@PreAuthorize("isAuthenticated()")
public class ProjectService {

    private final ProjectRepository projectRepository;

    ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public boolean hasProjects() {
        return projectRepository.count() > 0;
    }

    public List<ProjectListItem> findProjectListItems(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return projectRepository.findAllProjectListItems(pageable);
        } else {
            return projectRepository.findProjectListItemsBySearchTerm("%" + searchTerm + "%", pageable);
        }
    }

    public Optional<ProjectListItem> findProjectListItemById(Long projectId) {
        return projectRepository.findProjectListItemById(projectId);
    }

    public Long createProject(ProjectFormDataObject projectFormDataObject) {
        var project = new Project(projectFormDataObject.name());
        projectRepository.saveAndFlush(project);
        return project.requireId();
    }
}
