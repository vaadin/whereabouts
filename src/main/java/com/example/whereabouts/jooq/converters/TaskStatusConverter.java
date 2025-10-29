package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.jooq.enums.TaskStatus;

public final class TaskStatusConverter extends AbstractEnumConverter<TaskStatus, com.example.whereabouts.projects.TaskStatus> {

    public TaskStatusConverter() {
        super(TaskStatus.class, com.example.whereabouts.projects.TaskStatus.class);
    }
}
