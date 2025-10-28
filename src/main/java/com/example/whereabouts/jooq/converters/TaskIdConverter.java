package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.projects.TaskId;

public final class TaskIdConverter extends AbstractValueObjectConverter<Long, TaskId> {

    public TaskIdConverter() {
        super(Long.class, TaskId.class, TaskId::new, TaskId::value);
    }
}
