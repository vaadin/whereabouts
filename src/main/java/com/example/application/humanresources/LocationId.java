package com.example.application.humanresources;

import com.example.application.common.AbstractLongId;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NullMarked;

/**
 * Strongly-typed identifier for {@link Location} entities.
 * <p>
 * This wrapper around a long value provides type safety, preventing accidental mixing
 * of location IDs with other entity IDs or raw numeric values. The compiler will catch
 * errors like passing an employee ID where a location ID is expected.
 * <p>
 * Using dedicated ID types instead of primitives:
 * <ul>
 *   <li>Prevents ID confusion across different entity types</li>
 *   <li>Makes method signatures self-documenting</li>
 *   <li>Enables adding ID-specific behavior or validation</li>
 *   <li>Supports future changes to ID structure without breaking callers</li>
 * </ul>
 * <p>
 * The private constructor enforces creation through the factory method, ensuring
 * consistent instantiation and future extensibility (e.g., validation, caching).
 *
 * @see Location
 * @see AbstractLongId
 */
@NullMarked
public final class LocationId extends AbstractLongId {

    private LocationId(long value) {
        super(value);
    }

    /**
     * Creates a location ID from a long value.
     * <p>
     * This factory method is the sole way to create location IDs, enabling future
     * enhancements like validation or value caching without changing calling code.
     * The {@link JsonCreator} annotation ensures proper deserialization from JSON.
     *
     * @param value the numeric location identifier
     * @return a location ID wrapping the given value
     */
    @JsonCreator
    public static LocationId of(long value) {
        return new LocationId(value);
    }
}
