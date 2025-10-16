package com.example.whereabouts.projects;

import com.example.whereabouts.projects.internal.ProjectQuery;
import com.example.whereabouts.projects.internal.ProjectRepository;
import com.example.whereabouts.security.AppRoles;
import com.vaadin.flow.data.provider.SortOrder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

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
    public Stream<ProjectListItem> findProjectListItems(@Nullable String searchTerm, int limit, int offset, SortOrder<ProjectSortableProperty> sortOrder) {
        return projectQuery.findProjectListItemsBySearchTerm(searchTerm, limit, offset, sortOrder);
    }

    @Transactional(readOnly = true)
    public Optional<ProjectListItem> findProjectListItemById(ProjectId id) {
        return projectQuery.findProjectListItemById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.PROJECT_CREATE + "')")
    public ProjectId insert(ProjectData data) {
        return projectRepository.insert(data);
    }
}
