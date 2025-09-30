package com.example.application.common.address;

import com.example.application.common.Country;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jspecify.annotations.NullMarked;

@NullMarked
@JsonDeserialize(using = PostalAddressDeserializer.class)
public sealed interface PostalAddress permits CanadianPostalAddress, FinnishPostalAddress, GermanPostalAddress,
        InternationalPostalAddress, USPostalAddress {

    String PROP_COUNTRY = "country";

    Country country();

    String toFormattedString();
}
