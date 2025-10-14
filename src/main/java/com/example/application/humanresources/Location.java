package com.example.application.humanresources;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an office location in the organization's network of facilities.
 * <p>
 * A location encompasses both identifying information (ID and version for optimistic locking)
 * and mutable business data (name, type, address, facilities, etc.). This separation allows
 * for immutable entity references while supporting updates to location details.
 * <p>
 * Locations support optimistic locking through the version field, ensuring concurrent
 * modifications are detected and prevented.
 *
 * @param id      the unique identifier for this location
 * @param version the version number for optimistic locking; incremented on each update
 * @param data    the business data for this location (name, address, facilities, etc.)
 */
@NullMarked
public record Location(LocationId id, long version,
                       LocationData data) implements Entity<LocationId> {

    /**
     * Creates a new location instance with updated data while preserving identity and version.
     * <p>
     * This is useful for preparing updates before persisting - the repository will increment
     * the version during the actual update operation.
     *
     * @param data the new location data to associate with this location's identity
     * @return a new {@code Location} instance with the same ID and version, but updated data
     */
    public Location withData(LocationData data) {
        return new Location(id, version, data);
    }
}
