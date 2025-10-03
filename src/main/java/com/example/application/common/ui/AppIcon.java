package com.example.application.common.ui;

import com.vaadin.flow.component.icon.SvgIcon;

public enum AppIcon {
    ACCESSIBLE("icons/accessible.svg"),
    APARTMENT("icons/apartment.svg"),
    CALENDAR_MONTH("icons/calendar_month.svg"),
    DESK("icons/desk.svg"),
    DIVERSITY("icons/diversity.svg"),
    FLATWARE("icons/flatware.svg"),
    GLOBE_LOCATION_PIN("icons/globe_location_pin.svg"),
    MEETING_ROOM("icons/meeting_room.svg"),
    PARKING_SIGN("icons/parking_sign.svg");

    private final String source;

    AppIcon(String source) {
        this.source = source;
    }

    public SvgIcon create() {
        return new SvgIcon(source);
    }
}
