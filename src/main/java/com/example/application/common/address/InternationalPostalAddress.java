package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record InternationalPostalAddress(@Nullable String streetAddress, @Nullable String city,
                                         @Nullable String stateProvinceOrRegion, @Nullable String postalCode,
                                         Country country) implements PostalAddress {

    public static final int MAX_STRING_LENGTH = 150;
    public static final String PROP_STREET_ADDRESS = "streetAddress";
    public static final String PROP_CITY = "city";
    public static final String PROP_STATE_PROVINCE_OR_REGION = "stateProvinceOrRegion";
    public static final String PROP_POSTAL_CODE = "postalCode";

    public InternationalPostalAddress {
        requireMaxLength(streetAddress, PROP_STREET_ADDRESS);
        requireMaxLength(city, PROP_CITY);
        requireMaxLength(stateProvinceOrRegion, PROP_STATE_PROVINCE_OR_REGION);
        requireMaxLength(postalCode, PROP_POSTAL_CODE);
    }

    private static void requireMaxLength(@Nullable String value, String fieldName) {
        if (value != null && value.length() > MAX_STRING_LENGTH) {
            throw new IllegalArgumentException(fieldName + " is too long");
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
        if (stateProvinceOrRegion != null) {
            sb.append(stateProvinceOrRegion).append(" ");
        }
        if (postalCode != null) {
            sb.append(postalCode).append(", ");
        }
        sb.append(country.displayName());
        return sb.toString();
    }
}
