package com.example.application.common.address;

import com.example.application.common.Country;
import org.jspecify.annotations.NonNull;

public sealed interface PostalAddress permits CanadianPostalAddress, FinnishPostalAddress, GermanPostalAddress,
        InternationalPostalAddress, USPostalAddress {

    @NonNull
    Country country();
}
