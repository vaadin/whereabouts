package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.common.PhoneNumber;

public final class PhoneNumberConverter extends AbstractValueObjectConverter<String, PhoneNumber> {

    public PhoneNumberConverter() {
        super(String.class, PhoneNumber.class, PhoneNumber::of, PhoneNumber::toString);
    }
}
