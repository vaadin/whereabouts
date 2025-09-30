package com.example.application.humanresources;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum LocationType {
    REGIONAL_HQ("Regional headquarters"),
    BRANCH_OFFICE("Branch office"),
    REMOTE_HUB("Remote working hub"),
    GLOBAL_HQ("Global headquarters");

    private final String displayName;

    LocationType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
