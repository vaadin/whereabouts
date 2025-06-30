package com.example.application.taskmanagement.domain;

public enum TaskPriority {
    URGENT("Urgent"), HIGH("High"), NORMAL("Normal"), LOW("Low");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
