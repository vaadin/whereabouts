package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.jooq.enums.LocationType;

public final class LocationTypeConverter extends AbstractEnumConverter<LocationType, com.example.whereabouts.humanresources.LocationType> {

    public LocationTypeConverter() {
        super(LocationType.class, com.example.whereabouts.humanresources.LocationType.class);
    }
}
