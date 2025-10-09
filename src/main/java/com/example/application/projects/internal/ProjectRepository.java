package com.example.application.projects.internal;

import com.example.application.common.Repository;
import com.example.application.projects.Project;
import com.example.application.projects.ProjectData;
import com.example.application.projects.ProjectId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface ProjectRepository extends Repository {

    Optional<Project> findById(ProjectId id);

    ProjectId insert(ProjectData data);
}
