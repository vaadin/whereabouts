package com.example.application.common.address.germany;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class GermanPostalCode implements Serializable {

    public static final int LENGTH = 5;

    private final String value;

    private GermanPostalCode(String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (GermanPostalCode) o;
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

    public static GermanPostalCode of(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid postal code");
        }
        return new GermanPostalCode(value);
    }
}
