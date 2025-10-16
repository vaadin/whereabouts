package com.example.whereabouts.humanresources;

import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public final class EmploymentTypeFormatter {

    private static final EmploymentTypeFormatter INSTANCE = new EmploymentTypeFormatter();

    private EmploymentTypeFormatter() {
    }

    // Future-proofing the API in case the UI must support multiple locales in the future

    @SuppressWarnings("unused")
    public static EmploymentTypeFormatter ofLocale(Locale locale) {
        return INSTANCE;
    }

    public String getDisplayName(EmploymentType employmentType) {
        return switch (employmentType) {
            case FULL_TIME -> "Full Time";
            case PART_TIME -> "Part Time";
        };
    }
}
