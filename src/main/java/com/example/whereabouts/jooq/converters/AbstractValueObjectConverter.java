package com.example.whereabouts.jooq.converters;

import org.jooq.impl.AbstractConverter;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

abstract class AbstractValueObjectConverter<T, U> extends AbstractConverter<T, U> {

    private final Function<T, U> fromConverter;
    private final Function<U, T> toConverter;

    public AbstractValueObjectConverter(Class<T> fromType, Class<U> toType, Function<T, U> fromConverter, Function<U, T> toConverter) {
        super(fromType, toType);
        this.fromConverter = fromConverter;
        this.toConverter = toConverter;
    }

    @Override
    @Nullable
    public U from(@Nullable T t) {
        return t == null ? null : fromConverter.apply(t);
    }

    @Override
    @Nullable
    public T to(@Nullable U u) {
        return u == null ? null : toConverter.apply(u);
    }
}
