package com.example.application.taskmanagement.domain.event;

import com.example.application.taskmanagement.domain.Task;

public class TaskCreatedEvent extends TaskEvent {

    public TaskCreatedEvent(Task task) {
        super(task);
    }
}
