package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.jooq.enums.TaskPriority;

public final class TaskPriorityConverter extends AbstractEnumConverter<TaskPriority, com.example.whereabouts.projects.TaskPriority> {

    public TaskPriorityConverter() {
        super(TaskPriority.class, com.example.whereabouts.projects.TaskPriority.class);
    }
}
