package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.ValueObject;
import com.example.whereabouts.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public record LocationData(String name, LocationType locationType, PostalAddress address, LocalDate established,
                           String about, ZoneId timeZone, Collection<LocationFacility> facilities
) implements ValueObject {
    public static final String PROP_NAME = "name";
    public static final String PROP_LOCATION_TYPE = "locationType";
    public static final String PROP_ADDRESS = "address";
    public static final String PROP_ESTABLISHED = "established";
    public static final String PROP_ABOUT = "about";
    public static final String PROP_TIME_ZONE = "timeZone";
    public static final String PROP_FACILITIES = "facilities";
}
