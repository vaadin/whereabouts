package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record USPostalAddress(@Nullable String streetAddress, @Nullable String city, @Nullable USState state,
                              @Nullable USZipCode zipCode, Country country) implements PostalAddress {

    public static final int MAX_STRING_LENGTH = 150;
    public static final String PROP_STREET_ADDRESS = "streetAddress";
    public static final String PROP_CITY = "city";
    public static final String PROP_STATE = "state";
    public static final String PROP_ZIP_CODE = "zipCode";

    public USPostalAddress {
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (city != null && city.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("city is too long");
        }
    }
}
