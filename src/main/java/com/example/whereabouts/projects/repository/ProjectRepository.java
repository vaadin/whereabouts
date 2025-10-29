package com.example.whereabouts.projects.repository;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.projects.Project;
import com.example.whereabouts.projects.ProjectData;
import com.example.whereabouts.projects.ProjectId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 * @see "Design Decision: DD009-20251029-jooq-user-types.md"
 */
@NullMarked
public interface ProjectRepository extends Repository {

    Optional<Project> findById(ProjectId id);

    ProjectId insert(ProjectData data);
}
