package com.example.application.humanresources;

import com.example.application.common.ValueObject;
import com.example.application.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;

/**
 * Value object containing the business data for a {@link Location}.
 * <p>
 * This record encapsulates all mutable attributes of a location, separated from its
 * identity and version information. This separation enables immutable entity references
 * while allowing location details to be updated independently.
 * <p>
 * The property name constants support type-safe UI data binding in Vaadin forms,
 * preventing errors from string literals in binding expressions.
 *
 * @param name         the display name of the location (e.g., "Stockholm Office")
 * @param locationType the classification of this location (HQ, branch, hub, etc.)
 * @param address      the postal address where this location is situated
 * @param established  the date when this location was opened or established
 * @param about        descriptive text about the location, its purpose, or characteristics
 * @param timeZone     the time zone in which this location operates
 * @param facilities   the amenities and facilities available at this location
 */
@NullMarked
public record LocationData(String name, LocationType locationType, PostalAddress address, LocalDate established,
                           String about, ZoneId timeZone, Collection<LocationFacility> facilities
) implements ValueObject {
    /**
     * Property name constant for {@link #name()}, used for UI data binding.
     */
    public static final String PROP_NAME = "name";
    /**
     * Property name constant for {@link #locationType()}, used for UI data binding.
     */
    public static final String PROP_LOCATION_TYPE = "locationType";
    /**
     * Property name constant for {@link #address()}, used for UI data binding.
     */
    public static final String PROP_ADDRESS = "address";
    /**
     * Property name constant for {@link #established()}, used for UI data binding.
     */
    public static final String PROP_ESTABLISHED = "established";
    /**
     * Property name constant for {@link #about()}, used for UI data binding.
     */
    public static final String PROP_ABOUT = "about";
    /**
     * Property name constant for {@link #timeZone()}, used for UI data binding.
     */
    public static final String PROP_TIME_ZONE = "timeZone";
    /**
     * Property name constant for {@link #facilities()}, used for UI data binding.
     */
    public static final String PROP_FACILITIES = "facilities";
}
