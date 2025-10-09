package com.example.application.projects;

import com.example.application.projects.internal.ProjectQuery;
import com.example.application.projects.internal.ProjectRepository;
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
@PreAuthorize("hasRole('" + AppRoles.PROJECT_READ + "')")
@NullMarked
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectQuery projectQuery;

    ProjectService(ProjectRepository projectRepository, ProjectQuery projectQuery) {
        this.projectRepository = projectRepository;
        this.projectQuery = projectQuery;
    }

    @Transactional(readOnly = true)
    public List<ProjectListItem> findProjectListItems(@Nullable String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return projectQuery.findAllProjectListItems(pageable);
        } else {
            return projectQuery.findProjectListItemsBySearchTerm(searchTerm, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Optional<ProjectListItem> findProjectListItemById(ProjectId id) {
        return projectQuery.findProjectListItemById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.PROJECT_WRITE + "')")
    public ProjectId insert(ProjectData data) {
        return projectRepository.insert(data);
    }
}
