package com.example.whereabouts.jooq.converters;

import org.jooq.impl.AbstractConverter;
import org.jspecify.annotations.Nullable;

abstract class AbstractEnumConverter<T extends Enum<T>, U extends Enum<U>> extends AbstractConverter<T, U> {

    AbstractEnumConverter(Class<T> fromType, Class<U> toType) {
        super(fromType, toType);
    }

    @Override
    @Nullable
    public U from(@Nullable T t) {
        return t == null ? null : Enum.valueOf(toType(), t.name());
    }

    @Override
    @Nullable
    public T to(@Nullable U u) {
        return u == null ? null : Enum.valueOf(fromType(), u.name());
    }
}
