package com.example.application.common.address;

import com.example.application.common.Country;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@JsonDeserialize
public record USPostalAddress(@Nullable String streetAddress, @Nullable String city, @Nullable USState state,
                              @Nullable USZipCode zipCode, Country country) implements PostalAddress {

    public static final String ISO_CODE = "US";
    public static final int MAX_STRING_LENGTH = 150;
    public static final String PROP_STREET_ADDRESS = "streetAddress";
    public static final String PROP_CITY = "city";
    public static final String PROP_STATE = "state";
    public static final String PROP_ZIP_CODE = "zipCode";

    public USPostalAddress {
        if (!country.isoCode().equals(ISO_CODE)) {
            throw new IllegalArgumentException("country is invalid");
        }
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (city != null && city.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("city is too long");
        }
    }

    @Override
    public String toFormattedString() {
        var sb = new StringBuilder();
        if (streetAddress != null) {
            sb.append(streetAddress).append(", ");
        }
        if (city != null) {
            sb.append(city).append(", ");
        }
        if (state != null) {
            sb.append(state.name()).append(" ");
        }
        if (zipCode != null) {
            sb.append(zipCode).append(", ");
        }
        sb.append(country.displayName());
        return sb.toString();
    }
}
