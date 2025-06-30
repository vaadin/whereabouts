package com.example.application.taskmanagement.dto;

import java.util.Objects;

public record ProjectListItem(Long projectId, String projectName, long tasks, long assignees) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectListItem that = (ProjectListItem) o;
        return Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(projectId);
    }
}
