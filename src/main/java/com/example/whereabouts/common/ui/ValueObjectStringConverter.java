package com.example.whereabouts.common.ui;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/**
 * @see "Design decision: DD003-20241023-value-objects-and-validation.md"
 */
public class ValueObjectStringConverter<T> implements Converter<String, T> {

    private final Function<String, T> factory;

    public ValueObjectStringConverter(Function<String, T> factory) {
        this.factory = factory;
    }

    @Override
    public @NonNull Result<T> convertToModel(@Nullable String value, @NonNull ValueContext context) {
        try {
            return (value == null || value.isEmpty()) ? Result.ok(null) : Result.ok(factory.apply(value));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public @Nullable String convertToPresentation(@Nullable T model, @NonNull ValueContext context) {
        return model == null ? "" : model.toString();
    }
}
