package com.example.application.common.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.JSON;
import org.jooq.exception.DataAccessException;

import java.io.IOException;

public final class PostalAddressConverter implements Converter<JSON, PostalAddress> {

    private final static StableValue<PostalAddressConverter> INSTANCE = StableValue.of();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static PostalAddressConverter instance() {
        return INSTANCE.orElseSet(PostalAddressConverter::new);
    }

    @Override
    public PostalAddress from(JSON databaseObject) {
        try {
            return objectMapper.readerFor(PostalAddress.class).readValue(databaseObject.data());
        } catch (IOException ex) {
            throw new DataAccessException("Error reading address JSON", ex);
        }
    }

    @Override
    public JSON to(PostalAddress userObject) {
        if (userObject == null) {
            return null;
        }
        try {
            return JSON.json(objectMapper.writeValueAsString(userObject));
        } catch (IOException ex) {
            throw new DataAccessException("Error writing address JSON", ex);
        }
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
