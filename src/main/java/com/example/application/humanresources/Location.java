package com.example.application.humanresources;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Location(LocationId id, long version,
                       LocationData data) implements Entity<LocationId> {

    public Location withData(LocationData data) {
        return new Location(id, version, data);
    }
}
