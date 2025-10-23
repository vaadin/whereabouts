package com.example.whereabouts.common.address;

import com.example.whereabouts.common.ValueObject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * @see "Design decision: DD003-20251023-value-objects-and-validation.md"
 */
@NullMarked
public final class CanadianPostalCode implements ValueObject {

    public static final int LENGTH = 7;
    private static final Pattern REGEX = Pattern.compile("^[A-CEG-HJ-NPR-TVXY][0-9][A-CEG-HJ-NPR-TVXY] [0-9][A-CEG-HJ-NPR-TVW-Z][0-9]$");

    private final String value;

    CanadianPostalCode(String value) {
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
        var that = (CanadianPostalCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static boolean isValid(String value) {
        // Check length
        if (value.length() != LENGTH) {
            return false;
        }
        // All characters should be ASCII letters, ASCII digits or a space
        if (!value.chars().allMatch(ch -> (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || (ch == ' '))) {
            return false;
        }
        return REGEX.matcher(value).matches();
    }

    @JsonCreator
    public static CanadianPostalCode of(String value) {
        var sanitized = value.toUpperCase().strip();
        if (!isValid(sanitized)) {
            throw new IllegalArgumentException("Invalid postal code");
        }
        return new CanadianPostalCode(sanitized);
    }
}
