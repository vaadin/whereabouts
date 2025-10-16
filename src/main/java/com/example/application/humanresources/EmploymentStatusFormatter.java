package com.example.application.humanresources;

import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public final class EmploymentStatusFormatter {

    private static final EmploymentStatusFormatter INSTANCE = new EmploymentStatusFormatter();

    private EmploymentStatusFormatter() {
    }

    // Future-proofing the API in case the UI must support multiple locales in the future

    @SuppressWarnings("unused")
    public static EmploymentStatusFormatter ofLocale(Locale locale) {
        return INSTANCE;
    }

    public String getDisplayName(EmploymentStatus status) {
        return switch (status) {
            case TERMINATED -> "Terminated";
            case ACTIVE -> "Active";
            case INACTIVE -> "Inactive";
        };
    }
}
