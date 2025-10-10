package com.example.application.projects;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record Task(TaskId id, long version, TaskData data) implements Entity<TaskId> {

    public Task withData(TaskData data) {
        return new Task(id, version, data);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (Task) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
