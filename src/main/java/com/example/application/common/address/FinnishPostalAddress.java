package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record FinnishPostalAddress(@Nullable String streetAddress, @Nullable FinnishPostalCode postalCode,
                                   @Nullable String postOffice, Country country) implements PostalAddress {

    public static final int MAX_STRING_LENGTH = 150;
    public static final String PROP_STREET_ADDRESS = "streetAddress";
    public static final String PROP_POSTAL_CODE = "postalCode";
    public static final String PROP_POST_OFFICE = "postOffice";

    public FinnishPostalAddress {
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (postOffice != null && postOffice.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("postOffice is too long");
        }
    }
}
