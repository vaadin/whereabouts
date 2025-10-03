package com.example.application.common.address;

import com.example.application.common.Country;
import com.example.application.common.ValueObject;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

@NullMarked
@JsonDeserialize(using = JacksonPostalAddressDeserializer.class)
public sealed interface PostalAddress extends ValueObject permits CanadianPostalAddress, FinnishPostalAddress,
        GermanPostalAddress, InternationalPostalAddress, USPostalAddress {

    String PROP_COUNTRY = "country";

    Country country();

    String toFormattedString();
}
