package com.example.application.humanresources;

import com.example.application.common.ValueObject;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Represents an amenity or facility available at a {@link Location}.
 * <p>
 * This sealed interface uses algebraic data types to model different facility types,
 * each with its own specific attributes. Some facilities are quantified (e.g., number
 * of hot desks), while others represent boolean presence/absence (e.g., kitchen).
 * <p>
 * The sealed nature ensures exhaustive pattern matching at compile time, making it
 * impossible to forget handling a facility type when processing them. This is particularly
 * useful in mappers and UI components.
 * <p>
 * Facilities are stored in a separate database table and loaded as a collection with
 * their parent location. The Jackson annotations enable proper JSON serialization with
 * type discrimination for API responses, Flow signals, and potential future integration needs.
 *
 * @see Location
 * @see LocationData
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public sealed interface LocationFacility extends ValueObject {

    /**
     * Floor space available at the location, measured in square meters.
     * <p>
     * Represents the total usable office area, useful for capacity planning
     * and space utilization metrics.
     *
     * @param squareMeters the total floor area in square meters
     */
    @JsonTypeName("floor-space")
    record FloorSpace(int squareMeters) implements LocationFacility {
    }

    /**
     * Hot desks available for flexible seating arrangements.
     * <p>
     * Hot desks are unassigned workspaces that employees can use on a first-come,
     * first-served basis, supporting flexible and hybrid work arrangements.
     *
     * @param number the count of available hot desks
     */
    @JsonTypeName("hot-desks")
    record HotDesks(int number) implements LocationFacility {
    }

    /**
     * Kitchen facility available at the location.
     * <p>
     * Represents a boolean facility - either present or absent.
     */
    @JsonTypeName("kitchen")
    record Kitchen() implements LocationFacility {
    }

    /**
     * Small meeting booths for private calls or small group discussions.
     * <p>
     * Meeting booths are typically enclosed spaces for 1-4 people, suitable for
     * phone calls, video conferences, or small collaborative sessions.
     *
     * @param number the count of available meeting booths
     */
    @JsonTypeName("meeting-booths")
    record MeetingBooths(int number) implements LocationFacility {
    }

    /**
     * Accessible office features for employees with disabilities.
     * <p>
     * Indicates that the location has accessibility accommodations such as ramps,
     * elevators, accessible restrooms, and appropriately designed workspaces to
     * support employees with mobility, visual, or other disabilities.
     */
    @JsonTypeName("accessible-office")
    record AccessibleOffice() implements LocationFacility {
    }

    /**
     * Parking slots available at or near the location.
     * <p>
     * Includes all types of parking spaces (standard, accessible, EV charging, etc.)
     * in a single count. May be expanded in the future if individual parking slot
     * management becomes necessary.
     *
     * @param number the count of available parking slots
     */
    @JsonTypeName("parking-slots")
    record ParkingSlots(int number) implements LocationFacility {
    }
}
