package com.example.application.taskmanagement.domain.event;

import com.example.application.taskmanagement.domain.Task;

public abstract class TaskEvent {

    private final Task task;

    protected TaskEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
