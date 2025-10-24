package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Entity;
import org.jspecify.annotations.NullMarked;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public record Location(LocationId id, long version,
                       LocationData data) implements Entity<LocationId> {

    public Location withData(LocationData data) {
        return new Location(id, version, data);
    }
}
