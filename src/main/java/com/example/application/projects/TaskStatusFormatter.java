package com.example.application.projects;

import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public final class TaskStatusFormatter {

    private static final TaskStatusFormatter INSTANCE = new TaskStatusFormatter();

    private TaskStatusFormatter() {
    }

    // Future-proofing the API in case the UI must support multiple locales in the future

    @SuppressWarnings("unused")
    public static TaskStatusFormatter ofLocale(Locale locale) {
        return INSTANCE;
    }

    public String getDisplayName(TaskStatus status) {
        return switch (status) {
            case PENDING -> "Pending";
            case PLANNED -> "Planned";
            case IN_PROGRESS -> "In Progress";
            case PAUSED -> "Paused";
            case DONE -> "Done";
        };
    }
}
