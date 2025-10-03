package com.example.application.common;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for domain primitives that are used to identify other objects by wrapping a {@code long} value.
 */
public abstract class AbstractLongId implements Serializable {

    private final long value;

    protected AbstractLongId(long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Long.toString(value, 10);
    }

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