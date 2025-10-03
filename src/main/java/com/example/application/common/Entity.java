package com.example.application.common;

import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

/**
 * Marker interface for entities in Domain-Driven Design.
 * <p>
 * Entities are objects with a continuous identity throughout their lifecycle, regardless
 * of changes to their attributes. Two entities are the same if they have the same identity
 * (ID), even if all their other attributes differ. Entities are mutable - their state can
 * change over time while maintaining their identity.
 * <p>
 * All entities in this application have a strongly-typed {@link Identifier} that uniquely
 * identifies them within their type. This prevents accidental ID confusion and makes the
 * domain model more explicit.
 * <p>
 * Entities are typically persisted to a database and retrieved by their ID. The lifecycle
 * of an entity includes creation, modification, and eventual deletion, all while maintaining
 * the same identity.
 *
 * @param <ID> the type of identifier used by this entity
 * @see Identifier
 * @see ValueObject
 * @see AggregateRoot
 */
@NullMarked
public interface Entity<ID extends Identifier> extends Serializable {

    /**
     * Returns the unique identifier of this entity.
     * <p>
     * The ID remains constant throughout the entity's lifecycle and is used to determine
     * entity equality - two entities with the same ID are considered the same entity,
     * regardless of their other attributes.
     *
     * @return the entity's unique identifier
     */
    ID id();
}
