package com.example.application.common;

import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Domain primitive representing a phone number.
 */
public final class PhoneNumber implements Serializable {

    public static final int MAX_LENGTH = 16;

    private final String value;

    private PhoneNumber(@NonNull String value) {
        this.value = requireNonNull(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (PhoneNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Checks if the given string is a valid phone number.
     *
     * @param value the phone number to validate.
     * @return {@code true} if the phone number is valid, {@code false} otherwise.
     */
    public static boolean isValid(@NonNull String value) {
        // Check length
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            return false;
        }
        // Check format
        if (value.charAt(0) == '+') {
            return value.length() > 1 && areDigitsOnly(value.substring(1));
        } else {
            return areDigitsOnly(value);
        }
    }

    private static boolean areDigitsOnly(String s) {
        for (var c : s.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes commonly used formatting characters from the given phone number and returns it. Any other illegal
     * characters, such as letters, will be kept. To make sure the sanitized phone number is valid, you should
     * pass it through {@link #isValid(String)}.
     *
     * @param value the phone number to sanitize.
     * @return the sanitized phone number.
     */
    public static @NonNull String sanitize(@NonNull String value) {
        var sb = new StringBuilder();
        for (var c : value.toCharArray()) {
            if (!Character.isWhitespace(c) && c != '-' && c != '(' && c != ')' && c != '.') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * {@linkplain #sanitize(String) Sanitizes} the given string and creates a new {@code PhoneNumber} from it.
     *
     * @param value the phone number to create.
     * @return the new {@code PhoneNumber}.
     * @throws IllegalArgumentException if the value is not a valid phone number, even after sanitization.
     */
    public static @NonNull PhoneNumber of(@NonNull String value) {
        var sanitized = sanitize(value);
        if (!isValid(sanitized)) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        return new PhoneNumber(sanitized);
    }
}