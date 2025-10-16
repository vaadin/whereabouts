package com.example.whereabouts.projects.internal;

import com.example.whereabouts.projects.ProjectId;
import com.example.whereabouts.projects.ProjectListItem;
import com.example.whereabouts.projects.ProjectSortableProperty;
import com.vaadin.flow.data.provider.SortOrder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

@NullMarked
public interface ProjectQuery {

    Stream<ProjectListItem> findProjectListItemsBySearchTerm(@Nullable String searchTerm, int limit, int offset, SortOrder<ProjectSortableProperty> sortOrder);

    Optional<ProjectListItem> findProjectListItemById(ProjectId id);
}
