package com.example.application.taskmanagement.domain.event;

import com.example.application.taskmanagement.domain.Task;

public class TaskDeletedEvent extends TaskEvent {

    public TaskDeletedEvent(Task task) {
        super(task);
    }
}
