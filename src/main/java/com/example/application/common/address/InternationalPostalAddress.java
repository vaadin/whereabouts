package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record InternationalPostalAddress(@Nullable String streetAddress, @Nullable String city,
                                         @Nullable String stateProvinceOrRegion, @Nullable String postalCode,
                                         Country country) implements PostalAddress {

    public static final int MAX_STRING_LENGTH = 150;

    public InternationalPostalAddress {
        requireMaxLength(streetAddress, "streetAddress");
        requireMaxLength(city, "city");
        requireMaxLength(stateProvinceOrRegion, "stateProvinceOrRegion");
        requireMaxLength(postalCode, "postalCode");
    }

    private static void requireMaxLength(@Nullable String value, String fieldName) {
        if (value != null && value.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException(fieldName + " is too long");
        }
    }
}
