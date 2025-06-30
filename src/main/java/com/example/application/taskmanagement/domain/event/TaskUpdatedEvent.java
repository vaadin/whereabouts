package com.example.application.taskmanagement.domain.event;

import com.example.application.taskmanagement.domain.Task;

public class TaskUpdatedEvent extends TaskEvent {

    public TaskUpdatedEvent(Task task) {
        super(task);
    }
}
