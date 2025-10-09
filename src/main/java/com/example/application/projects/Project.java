package com.example.application.projects;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Project(ProjectId id, long version, ProjectData data) implements Entity<ProjectId> {
}
