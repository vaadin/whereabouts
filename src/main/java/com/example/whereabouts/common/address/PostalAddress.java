package com.example.whereabouts.common.address;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.ValueObject;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

/**
 * @see "Design decision: DD003-20251023-value-objects-and-validation.md"
 */
@NullMarked
@JsonDeserialize(using = JacksonPostalAddressDeserializer.class)
public sealed interface PostalAddress extends ValueObject permits CanadianPostalAddress, FinnishPostalAddress,
        GermanPostalAddress, InternationalPostalAddress, USPostalAddress {

    String PROP_COUNTRY = "country";

    Country country();

    String toFormattedString();
}
