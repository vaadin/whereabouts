package com.example.application.humanresources;

import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public final class WorkArrangementFormatter {

    private static final WorkArrangementFormatter INSTANCE = new WorkArrangementFormatter();

    private WorkArrangementFormatter() {
    }

    // Future-proofing the API in case the UI must support multiple locales in the future

    @SuppressWarnings("unused")
    public static WorkArrangementFormatter ofLocale(Locale locale) {
        return INSTANCE;
    }

    public String getDisplayName(WorkArrangement workArrangement) {
        return switch (workArrangement) {
            case HYBRID -> "Hybrid";
            case REMOTE -> "Remote";
            case ONSITE -> "On-site";
        };
    }
}
