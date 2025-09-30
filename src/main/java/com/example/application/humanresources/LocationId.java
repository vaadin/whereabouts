package com.example.application.humanresources;

import com.example.application.common.AbstractLongId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LocationId extends AbstractLongId {

    private LocationId(long value) {
        super(value);
    }

    public static LocationId of(long value) {
        return new LocationId(value);
    }
}
