package com.example.whereabouts.projects.internal;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.projects.Project;
import com.example.whereabouts.projects.ProjectData;
import com.example.whereabouts.projects.ProjectId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface ProjectRepository extends Repository {

    Optional<Project> findById(ProjectId id);

    ProjectId insert(ProjectData data);
}
