package com.example.whereabouts.common;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * Base class for value objects that are used to identify other objects by wrapping a {@code long} value.
 */
public abstract class AbstractLongId implements Identifier {

    private final long value;

    protected AbstractLongId(long value) {
        this.value = value;
    }

    /**
     * Returns the string representation of the ID.
     *
     * @return the ID as a string
     */
    @Override
    public String toString() {
        return Long.toString(value, 10);
    }

    /**
     * Returns the ID as a {@code long}.
     *
     * @return the ID as a {@code long}
     */
    @JsonValue
    public long toLong() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (AbstractLongId) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}