package com.example.application.common.address.canada;

import com.example.application.common.Country;
import com.example.application.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record CanadianPostalAddress(@Nullable String streetAddress, @Nullable String city,
                                    @Nullable CanadianProvince province,
                                    @Nullable CanadianPostalCode postalCode, Country country) implements PostalAddress {

    static final int MAX_STRING_LENGTH = 150;

    public CanadianPostalAddress {
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (city != null && city.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("city is too long");
        }
    }
}
