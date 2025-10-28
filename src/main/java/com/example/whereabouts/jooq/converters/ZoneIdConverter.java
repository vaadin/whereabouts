package com.example.whereabouts.jooq.converters;

import java.time.ZoneId;

public final class ZoneIdConverter extends AbstractValueObjectConverter<String, ZoneId> {

    public ZoneIdConverter() {
        super(String.class, ZoneId.class, ZoneId::of, ZoneId::getId);
    }
}
