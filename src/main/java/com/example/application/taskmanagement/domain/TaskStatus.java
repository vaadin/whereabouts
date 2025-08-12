package com.example.application.taskmanagement.domain;

public enum TaskStatus {
    PENDING("Pending"), PLANNED("Planned"), IN_PROGRESS("In progress"), PAUSED("Paused"), DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
