package com.example.application.taskmanagement;

public enum TaskStatus {
    PENDING("Pending"), IN_PROGRESS("In progress"), PAUSED("Paused"), DONE("Done");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
