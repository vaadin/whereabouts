package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.humanresources.LocationId;

public final class LocationIdConverter extends AbstractValueObjectConverter<Long, LocationId> {

    public LocationIdConverter() {
        super(Long.class, LocationId.class, LocationId::new, LocationId::value);
    }
}
