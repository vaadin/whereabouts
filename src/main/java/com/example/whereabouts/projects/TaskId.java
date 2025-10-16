package com.example.whereabouts.projects;

import com.example.whereabouts.common.AbstractLongId;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TaskId extends AbstractLongId {

    private TaskId(long value) {
        super(value);
    }

    @JsonCreator
    public static TaskId of(long value) {
        return new TaskId(value);
    }
}
