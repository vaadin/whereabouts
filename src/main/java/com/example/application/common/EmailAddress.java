package com.example.application.common;

import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Domain primitive representing a valid e-mail address.
 */
public final class EmailAddress implements Serializable {

    public static final int MAX_LENGTH = 320; // local name 64 bytes, @ 1 byte, domain name 255 bytes

    private final String value;

    private EmailAddress(@NonNull String value) {
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
        var that = (EmailAddress) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Checks if the given string is a valid e-mail address.
     *
     * @param value the e-mail address to validate.
     * @return {@code true} if the e-mail address is valid, {@code false} otherwise.
     */
    public static boolean isValid(@NonNull String value) {
        // Check length
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            return false;
        }
        var parts = value.split("@");
        // Check number of parts
        if (parts.length != 2) {
            return false;
        }
        // Validate parts
        return isValidLocalPart(parts[0]) && isValidDomainName(parts[1]);
    }

    /**
     * Note! Comments and quoted local parts are not supported (yet).
     */
    private static boolean isValidLocalPart(@NonNull String localPart) {
        // Check length
        if (localPart.isEmpty() || localPart.length() > 64) {
            return false;
        }
        // Check for invalid characters
        if (!localPart.matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+")) {
            return false;
        }
        // Check for double dots
        if (localPart.contains("..")) {
            return false;
        }
        // Check for leading or trailing dots
        return !localPart.startsWith(".") && !localPart.endsWith(".");
    }

    private static boolean isValidDomainName(@NonNull String domainName) {
        // Check length
        if (domainName.isEmpty() || domainName.length() > 255) {
            return false;
        }
        // Is it an IP address?
        if (domainName.startsWith("[")) {
            if (!domainName.endsWith("]")) {
                return false;
            }
            if (domainName.startsWith("[IPv6:")) {
                return IpAddress.Ipv6.isValidIpv6(domainName.substring(6, domainName.length() - 1));
            } else {
                return IpAddress.Ipv4.isValidIpv4(domainName.substring(1, domainName.length() - 1));
            }
        } else {
            return DomainName.isValid(domainName);
        }
    }

    /**
     * Creates a new {@code EmailAddress} from the given string.
     *
     * @param value the e-mail address to create.
     * @return the new {@code EmailAddress}.
     * @throws IllegalArgumentException if the value is not a valid e-mail address.
     */
    public static @NonNull EmailAddress of(@NonNull String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("Invalid e-mail address");
        }
        return new EmailAddress(value);
    }
}