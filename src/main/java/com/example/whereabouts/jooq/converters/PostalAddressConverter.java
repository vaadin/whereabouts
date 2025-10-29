package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.common.address.PostalAddress;
import org.jooq.JSON;
import org.jooq.impl.AbstractConverter;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.ObjectMapper;

public final class PostalAddressConverter extends AbstractConverter<JSON, PostalAddress> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PostalAddressConverter() {
        super(JSON.class, PostalAddress.class);
    }

    @Override
    @Nullable
    public PostalAddress from(@Nullable JSON databaseObject) {
        return databaseObject == null ? null : objectMapper.readerFor(PostalAddress.class).readValue(databaseObject.data());
    }

    @Override
    @Nullable
    public JSON to(@Nullable PostalAddress userObject) {
        if (userObject == null) {
            return null;
        }
        return JSON.json(objectMapper.writeValueAsString(userObject));
    }
}
