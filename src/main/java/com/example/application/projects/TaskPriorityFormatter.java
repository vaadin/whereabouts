package com.example.application.projects;

import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public final class TaskPriorityFormatter {

    private static final TaskPriorityFormatter INSTANCE = new TaskPriorityFormatter();

    private TaskPriorityFormatter() {
    }

    // Future-proofing the API in case the UI must support multiple locales in the future

    @SuppressWarnings("unused")
    public static TaskPriorityFormatter ofLocale(Locale locale) {
        return INSTANCE;
    }

    public String getDisplayName(TaskPriority priority) {
        return switch (priority) {
            case URGENT -> "Urgent";
            case HIGH -> "High";
            case NORMAL -> "Normal";
            case LOW -> "Low";
        };
    }
}
