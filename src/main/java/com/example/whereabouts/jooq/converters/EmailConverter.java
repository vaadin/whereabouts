package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.common.EmailAddress;

public final class EmailConverter extends AbstractValueObjectConverter<String, EmailAddress> {

    public EmailConverter() {
        super(String.class, EmailAddress.class, EmailAddress::of, EmailAddress::toString);
    }
}
