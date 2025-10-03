package com.example.application.common;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Value object representing a domain name.
 */
@NullMarked
public final class DomainName implements ValueObject {

    public static final int MAX_LENGTH = 253;

    private final String value;

    private DomainName(String value) {
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
        var that = (DomainName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Checks if the given string is a valid domain name (without actually checking that the domain name exists).
     *
     * @param value the string to check
     * @return {@code true} if the string is a valid domain name, {@code false} otherwise
     */
    public static boolean isValid(String value) {
        // Check length
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            return false;
        }
        var labels = value.split("\\.", -1);
        for (var label : labels) {
            // Check label length
            if (label.isEmpty() || label.length() > 63) {
                return false;
            }
            // Check label characters (only ASCII letters, digits, and - are allowed)
            for (var c : label.toCharArray()) {
                if (!Character.isDigit(c) && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != '-') {
                    return false;
                }
            }
            // Check that label does not start or end with a -
            if (label.charAt(0) == '-' || label.charAt(label.length() - 1) == '-') {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new {@code DomainName} from the given string.
     *
     * @param value the domain name to create
     * @return the new {@code DomainName}
     * @throws IllegalArgumentException if the string is not a valid domain name
     */
    public static DomainName of(String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid domain name");
        }
        return new DomainName(value);
    }
}