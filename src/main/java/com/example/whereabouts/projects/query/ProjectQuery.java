package com.example.whereabouts.projects.query;

import com.example.whereabouts.projects.ProjectId;
import com.example.whereabouts.projects.ProjectListItem;
import com.example.whereabouts.projects.ProjectSortableProperty;
import com.vaadin.flow.data.provider.SortOrder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @see "Design Decision: DD009-20251029-jooq-user-types.md"
 */
@NullMarked
public interface ProjectQuery {

    Stream<ProjectListItem> findProjectListItemsBySearchTerm(@Nullable String searchTerm, int limit, int offset, SortOrder<ProjectSortableProperty> sortOrder);

    Optional<ProjectListItem> findProjectListItemById(ProjectId id);
}
