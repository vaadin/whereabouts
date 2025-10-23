package com.example.whereabouts.common.address;

import com.example.whereabouts.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.annotation.JsonDeserialize;

/**
 * @see "Design decision: DD003-20251023-value-objects-and-validation.md"
 */
@NullMarked
@JsonDeserialize
public record CanadianPostalAddress(@Nullable String streetAddress, @Nullable String city,
                                    @Nullable CanadianProvince province,
                                    @Nullable CanadianPostalCode postalCode, Country country) implements PostalAddress {

    public static final String ISO_CODE = "CA";
    public static final String PROP_STREET_ADDRESS = "streetAddress";
    public static final String PROP_CITY = "city";
    public static final String PROP_PROVINCE = "province";
    public static final String PROP_POSTAL_CODE = "postalCode";

    public static final int MAX_STRING_LENGTH = 150;

    public CanadianPostalAddress {
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
        if (province != null) {
            sb.append(province.name()).append(" ");
        }
        if (postalCode != null) {
            sb.append(postalCode).append(", ");
        }
        sb.append(country.displayName());
        return sb.toString();
    }
}
