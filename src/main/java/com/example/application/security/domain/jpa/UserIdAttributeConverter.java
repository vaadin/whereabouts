package com.example.application.security.domain.jpa;

import com.example.application.security.domain.UserId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserIdAttributeConverter implements AttributeConverter<UserId, String> {

    @Override
    public String convertToDatabaseColumn(UserId userId) {
        return userId == null ? null : userId.toString();
    }

    @Override
    public UserId convertToEntityAttribute(String s) {
        return s == null ? null : UserId.of(s);
    }
}
