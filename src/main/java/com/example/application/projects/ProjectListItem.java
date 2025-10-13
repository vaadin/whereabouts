package com.example.application.projects;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record ProjectListItem(ProjectId projectId, String projectName, String description, int tasks, int assignees) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectListItem that = (ProjectListItem) o;
        return Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(projectId);
    }
}
