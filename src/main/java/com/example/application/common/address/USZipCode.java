package com.example.application.common.address;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

@NullMarked
public final class USZipCode implements Serializable {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 5;
    private static final Pattern REGEX = Pattern.compile("^[0-9]{5}(-[0-9]{4})?$");

    private final String value;

    private USZipCode(String value) {
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
        var that = (USZipCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    public static boolean isValid(String value) {
        // Check length
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            return false;
        }
        // All characters should be ASCII digits or a '-'
        if (!value.chars().allMatch(ch -> (ch >= '0' && ch <= '9') || (ch == '-'))) {
            return false;
        }
        return REGEX.matcher(value).matches();
    }

    @JsonCreator
    public static USZipCode of(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid zip code");
        }
        return new USZipCode(value);
    }
}