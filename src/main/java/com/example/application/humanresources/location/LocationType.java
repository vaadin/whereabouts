package com.example.application.humanresources.location;

import com.example.application.common.ValueObject;
import org.jspecify.annotations.NullMarked;

/**
 * Classification types for office {@link Location}s within the organization's network.
 * <p>
 * This enum categorizes locations by their role and scope in the organizational
 * hierarchy, from global headquarters down to remote working hubs. The classification
 * affects resource allocation, reporting hierarchies, and operational considerations.
 * <p>
 * Each type includes a human-readable display name suitable for UI presentation,
 * avoiding the need to convert enum constants to display text in view layers.
 *
 * @see Location
 * @see LocationData
 */
@NullMarked
public enum LocationType implements ValueObject {
    /**
     * The organization's primary global headquarters.
     */
    GLOBAL_HQ("Global headquarters"),
    /**
     * Regional headquarters serving a geographic area or business division.
     */
    REGIONAL_HQ("Regional headquarters"),
    /**
     * Standard branch office providing local presence and services.
     */
    BRANCH_OFFICE("Branch office"),
    /**
     * Smaller facility focused on enabling remote and hybrid work arrangements.
     */
    REMOTE_HUB("Remote working hub");

    private final String displayName;

    LocationType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this location type.
     * <p>
     * Use this in UI components to present location types to users, rather than
     * displaying the enum constant name directly.
     *
     * @return the display name (e.g., "Global headquarters")
     */
    public String displayName() {
        return displayName;
    }
}
