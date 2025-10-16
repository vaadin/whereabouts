package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.ValueObject;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public sealed interface LocationFacility extends ValueObject {

    @JsonTypeName("floor-space")
    record FloorSpace(int squareMeters) implements LocationFacility {
    }

    @JsonTypeName("hot-desks")
    record HotDesks(int number) implements LocationFacility {
    }

    @JsonTypeName("kitchen")
    record Kitchen() implements LocationFacility {
    }

    @JsonTypeName("meeting-booths")
    record MeetingBooths(int number) implements LocationFacility {
    }

    @JsonTypeName("accessible-office")
    record AccessibleOffice() implements LocationFacility {
    }

    @JsonTypeName("parking-slots")
    record ParkingSlots(int number) implements LocationFacility {
    }
}
