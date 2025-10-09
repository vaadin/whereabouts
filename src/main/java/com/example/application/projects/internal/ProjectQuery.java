package com.example.application.projects.internal;

import com.example.application.projects.ProjectId;
import com.example.application.projects.ProjectListItem;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@NullMarked
public interface ProjectQuery {

    List<ProjectListItem> findAllProjectListItems(Pageable pageable);

    List<ProjectListItem> findProjectListItemsBySearchTerm(String searchTerm, Pageable pageable);

    Optional<ProjectListItem> findProjectListItemById(ProjectId id);
}
