package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record CanadianPostalAddress(@Nullable String streetAddress, @Nullable String city,
                                    @Nullable CanadianProvince province,
                                    @Nullable CanadianPostalCode postalCode, Country country) implements PostalAddress {

    public static final String PROP_STREET_ADDRESS = "streetAddress";
    public static final String PROP_CITY = "city";
    public static final String PROP_PROVINCE = "province";
    public static final String PROP_POSTAL_CODE = "postalCode";

    public static final int MAX_STRING_LENGTH = 150;

    public CanadianPostalAddress {
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (city != null && city.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("city is too long");
        }
    }
}
