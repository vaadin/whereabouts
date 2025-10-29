package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.jooq.enums.Gender;

public final class GenderConverter extends AbstractEnumConverter<Gender, com.example.whereabouts.common.Gender> {

    public GenderConverter() {
        super(Gender.class, com.example.whereabouts.common.Gender.class);
    }
}
