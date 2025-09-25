package com.example.application.common.address.finland;

import com.example.application.common.Country;
import com.example.application.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record FinnishPostalAddress(@Nullable String streetAddress, @Nullable FinnishPostalCode postalCode,
                                   @Nullable String postOffice, Country country) implements PostalAddress {

    public static final int MAX_STRING_LENGTH = 150;

    public FinnishPostalAddress {
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (postOffice != null && postOffice.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("postOffice is too long");
        }
    }
}
