package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface PostalAddress permits CanadianPostalAddress, FinnishPostalAddress, GermanPostalAddress,
        InternationalPostalAddress, USPostalAddress {

    String PROP_COUNTRY = "country";

    Country country();

    String toFormattedString();
}
