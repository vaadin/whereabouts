package com.example.application.taskmanagement.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

class ProjectTasksChangedEvent extends ComponentEvent<Component> {

    private final Long projectId;

    public ProjectTasksChangedEvent(Component source, Long projectId) {
        super(source, false);
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }
}
