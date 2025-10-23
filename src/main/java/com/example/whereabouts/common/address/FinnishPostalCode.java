package com.example.whereabouts.common.address;

import com.example.whereabouts.common.ValueObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * @see "Design decision: DD003-20241023-value-objects-and-validation.md"
 */
@NullMarked
public final class FinnishPostalCode implements ValueObject {

    public static final int LENGTH = 5;

    private final String value;

    private FinnishPostalCode(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (FinnishPostalCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static boolean isValid(String value) {
        if (value.length() != LENGTH) {
            return false;
        }
        // All characters should be ASCII digits
        return value.chars().allMatch(ch -> (ch >= '0' && ch <= '9'));
    }

    @JsonCreator
    public static FinnishPostalCode of(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid postal code");
        }
        return new FinnishPostalCode(value);
    }
}
