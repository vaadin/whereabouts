package com.example.application.taskmanagement.domain.event;

import com.example.application.taskmanagement.domain.Project;

public class ProjectUpdatedEvent extends ProjectEvent {

    public ProjectUpdatedEvent(Project project) {
        super(project);
    }
}
