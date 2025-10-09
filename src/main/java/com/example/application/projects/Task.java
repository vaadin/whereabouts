package com.example.application.projects;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Task(TaskId id, long version, TaskData data) implements Entity<TaskId> {

    public Task withData(TaskData data) {
        return new Task(id, version, data);
    }
}
