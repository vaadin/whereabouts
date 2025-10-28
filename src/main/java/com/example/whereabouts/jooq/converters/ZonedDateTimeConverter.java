package com.example.whereabouts.jooq.converters;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class ZonedDateTimeConverter extends AbstractValueObjectConverter<OffsetDateTime, ZonedDateTime> {

    public ZonedDateTimeConverter() {
        super(OffsetDateTime.class, ZonedDateTime.class, OffsetDateTime::toZonedDateTime, ZonedDateTime::toOffsetDateTime);
    }
}
