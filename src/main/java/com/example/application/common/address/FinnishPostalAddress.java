package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.annotation.JsonDeserialize;

@NullMarked
@JsonDeserialize
public record FinnishPostalAddress(@Nullable String streetAddress, @Nullable FinnishPostalCode postalCode,
                                   @Nullable String postOffice, Country country) implements PostalAddress {

    public static final String ISO_CODE = "FI";
    public static final int MAX_STRING_LENGTH = 150;
    public static final String PROP_STREET_ADDRESS = "streetAddress";
    public static final String PROP_POSTAL_CODE = "postalCode";
    public static final String PROP_POST_OFFICE = "postOffice";

    public FinnishPostalAddress {
        if (!country.isoCode().equals(ISO_CODE)) {
            throw new IllegalArgumentException("country is invalid");
        }
        if (streetAddress != null && streetAddress.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("streetAddress is too long");
        }
        if (postOffice != null && postOffice.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException("postOffice is too long");
        }
    }

    @Override
    public String toFormattedString() {
        var sb = new StringBuilder();
        if (streetAddress != null) {
            sb.append(streetAddress).append(", ");
        }
        if (postalCode != null) {
            sb.append(postalCode).append(" ");
        }
        if (postOffice != null) {
            sb.append(postOffice).append(", ");
        }
        sb.append(country.displayName());
        return sb.toString();
    }
}
