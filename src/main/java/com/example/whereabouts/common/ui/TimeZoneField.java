package com.example.whereabouts.common.ui;

import com.vaadin.flow.component.combobox.ComboBox;

import java.time.ZoneId;

public final class TimeZoneField extends ComboBox<ZoneId> {

    public TimeZoneField() {
        setItems(ZoneId.getAvailableZoneIds().stream().sorted().map(ZoneId::of).toList());
    }
}
