package com.example.application.humanresources;

import com.example.application.common.AbstractLongId;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LocationId extends AbstractLongId {

    private LocationId(long value) {
        super(value);
    }

    @JsonCreator
    public static LocationId of(long value) {
        return new LocationId(value);
    }
}
