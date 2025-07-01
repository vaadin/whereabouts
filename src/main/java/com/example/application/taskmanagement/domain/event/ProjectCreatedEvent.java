package com.example.application.taskmanagement.domain.event;

import com.example.application.taskmanagement.domain.Project;

public class ProjectCreatedEvent extends ProjectEvent {

    public ProjectCreatedEvent(Project project) {
        super(project);
    }
}
