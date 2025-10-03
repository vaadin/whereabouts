package com.example.application.common;

import org.jspecify.annotations.NullMarked;

/**
 * Marker interface for aggregate roots in Domain-Driven Design.
 * <p>
 * An aggregate root is the entry point to an aggregate - a cluster of associated entities
 * and value objects that are treated as a single unit for data changes. All external access
 * to the aggregate goes through the aggregate root, which enforces invariants and maintains
 * consistency within the aggregate boundary.
 * <p>
 * Aggregate roots in this application follow a consistent pattern:
 * <ul>
 *   <li><strong>Identity</strong> - A strongly-typed {@link Identifier}</li>
 *   <li><strong>Version</strong> - For optimistic locking in concurrent scenarios</li>
 *   <li><strong>Data</strong> - Business attributes encapsulated in an immutable record</li>
 * </ul>
 * <p>
 * This structure separates entity identity and concurrency control from business data,
 * enabling immutable value objects while supporting entity lifecycle management. The
 * version field is incremented on each update, allowing the persistence layer to detect
 * and prevent conflicting concurrent modifications.
 *
 * @param <ID>   the type of identifier for this aggregate root
 * @param <DATA> the record type containing the business data for this aggregate
 * @see Entity
 * @see Identifier
 * @see ValueObject
 */
@NullMarked
public interface AggregateRoot<ID extends Identifier, DATA extends Record> extends Entity<ID> {

    /**
     * Returns the version number for optimistic locking.
     * <p>
     * The version is incremented each time the aggregate is updated and persisted.
     * When updating an aggregate, the persistence layer verifies that the version
     * in the database matches the version being updated, preventing lost updates
     * in concurrent modification scenarios.
     *
     * @return the current version number
     */
    long version();

    /**
     * Returns the business data for this aggregate root.
     * <p>
     * The data record contains all mutable business attributes of the aggregate,
     * separated from its identity and version. This enables immutable value objects
     * while supporting updates through a "copy with new data" pattern.
     *
     * @return the business data record
     */
    DATA data();
}
