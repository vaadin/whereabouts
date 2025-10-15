package com.example.application.humanresources;

import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public final class LocationTypeFormatter {

    private static final LocationTypeFormatter INSTANCE = new LocationTypeFormatter();

    private LocationTypeFormatter() {
    }

    // Future-proofing the API in case the UI must support multiple locales in the future

    @SuppressWarnings("unused")
    public static LocationTypeFormatter ofLocale(Locale locale) {
        return INSTANCE;
    }

    public String getDisplayName(LocationType locationType) {
        return switch (locationType) {
            case GLOBAL_HQ -> "Global headquarters";
            case REGIONAL_HQ -> "Regional headquarters";
            case BRANCH_OFFICE -> "Branch office";
            case REMOTE_HUB -> "Remote Hub";
        };
    }
}
