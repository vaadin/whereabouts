package com.example.whereabouts.security;

import com.example.whereabouts.common.AbstractLongId;
import com.fasterxml.jackson.annotation.JsonCreator;

public final class UserId extends AbstractLongId {

    private UserId(long value) {
        super(value);
    }

    @JsonCreator
    public static UserId of(long value) {
        return new UserId(value);
    }
}
