package com.example.application.humanresources;

import com.example.application.common.ValueObject;

public enum LocationType implements ValueObject {
    /**
     * The organization's primary global headquarters.
     */
    GLOBAL_HQ,
    /**
     * Regional headquarters serving a geographic area or business division.
     */
    REGIONAL_HQ,
    /**
     * Standard branch office providing local presence and services.
     */
    BRANCH_OFFICE,
    /**
     * Smaller facility focused on enabling remote and hybrid work arrangements.
     */
    REMOTE_HUB;
}
