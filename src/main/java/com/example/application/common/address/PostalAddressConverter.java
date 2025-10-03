package com.example.application.common.address;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.JSON;
import tools.jackson.databind.ObjectMapper;

public final class PostalAddressConverter implements Converter<JSON, PostalAddress> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PostalAddress from(JSON databaseObject) {
        return objectMapper.readerFor(PostalAddress.class).readValue(databaseObject.data());
    }

    @Override
    public JSON to(PostalAddress userObject) {
        if (userObject == null) {
            return null;
        }
        return JSON.json(objectMapper.writeValueAsString(userObject));
    }

    @Override
    public @NotNull Class<JSON> fromType() {
        return JSON.class;
    }

    @Override
    public @NotNull Class<PostalAddress> toType() {
        return PostalAddress.class;
    }
}
