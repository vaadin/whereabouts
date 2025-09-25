package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record GermanPostalAddress(@Nullable String streetAddress, @Nullable GermanPostalCode postalCode,
                                  @Nullable String city, Country country) implements PostalAddress {

    public static final int MAX_STRING_LENGTH = 150;

    public GermanPostalAddress {
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (city != null && city.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("city is too long");
        }
    }
}
