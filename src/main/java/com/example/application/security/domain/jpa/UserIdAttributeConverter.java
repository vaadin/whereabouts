package com.example.application.security.domain.jpa;

import com.example.application.security.domain.UserId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;

@Converter
public class UserIdAttributeConverter implements AttributeConverter<UserId, String> {

    @Override
    public @Nullable String convertToDatabaseColumn(@Nullable UserId userId) {
        return userId == null ? null : userId.toString();
    }

    @Override
    public @Nullable UserId convertToEntityAttribute(@Nullable String s) {
        return s == null ? null : UserId.of(s);
    }
}
