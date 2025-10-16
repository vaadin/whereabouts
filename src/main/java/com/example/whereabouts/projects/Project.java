package com.example.whereabouts.projects;

import com.example.whereabouts.common.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record Project(ProjectId id, long version, ProjectData data) implements Entity<ProjectId> {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (Project) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
