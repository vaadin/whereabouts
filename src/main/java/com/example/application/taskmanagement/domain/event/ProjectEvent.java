package com.example.application.taskmanagement.domain.event;

import com.example.application.taskmanagement.domain.Project;

public abstract class ProjectEvent {

    private final Project project;

    protected ProjectEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
