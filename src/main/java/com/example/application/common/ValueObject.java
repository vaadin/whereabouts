package com.example.application.common;

import java.io.Serializable;

/**
 * Marker interface for value objects in Domain-Driven Design.
 * <p>
 * Value objects are immutable objects defined by their attributes rather than identity.
 * Two value objects with the same attributes are considered equal, regardless of whether
 * they are the same object instance. Value objects have no conceptual identity and cannot
 * be modified - any "change" creates a new value object.
 * <p>
 * Value objects are serializable to support JSON serialization for API responses and
 * potential caching or session storage.
 *
 * @see Entity
 * @see Identifier
 */
public interface ValueObject extends Serializable {
}
