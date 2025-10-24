package com.example.whereabouts.projects;

import com.example.whereabouts.common.AbstractLongId;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NullMarked;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
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
