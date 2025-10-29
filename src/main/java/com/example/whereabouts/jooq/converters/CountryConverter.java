package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.common.Country;

public final class CountryConverter extends AbstractValueObjectConverter<String, Country> {

    public CountryConverter() {
        super(String.class, Country.class, Country::ofIsoCode, Country::toString);
    }
}
