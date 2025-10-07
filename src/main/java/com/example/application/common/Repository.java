package com.example.application.common;

import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * Base repository interface for aggregate roots in Domain-Driven Design.
 * <p>
 * Repositories provide collection-like access to aggregate roots, abstracting the
 * underlying persistence mechanism. Repositories operate at the aggregate boundary -
 * they save and retrieve entire aggregates as units, maintaining consistency within
 * the aggregate.
 * <p>
 * This interface defines the standard CRUD operations for aggregate roots:
 * <ul>
 *   <li><strong>Query</strong> - Check existence and retrieve by ID</li>
 *   <li><strong>Insert</strong> - Create new aggregates from data</li>
 *   <li><strong>Update</strong> - Persist changes to existing aggregates</li>
 *   <li><strong>Delete</strong> - Remove aggregates by ID</li>
 * </ul>
 * <p>
 * The separation between insert (taking DATA) and update (taking the full aggregate root)
 * reflects the lifecycle: new aggregates don't yet have identity or version, while updates
 * operate on existing aggregates with established identity and version for optimistic locking.
 * <p>
 * Implementations handle persistence concerns like transaction management, optimistic
 * locking verification, and mapping between domain objects and database representations.
 * The interface itself remains persistence-agnostic, allowing different storage strategies.
 *
 * @param <ID>   the identifier type for the aggregate root
 * @param <AR>   the aggregate root type managed by this repository
 * @param <DATA> the business data record type contained in the aggregate root
 * @see AggregateRoot
 * @see Identifier
 */
@NullMarked
public interface Repository<ID extends Identifier, AR extends AggregateRoot<ID, DATA>, DATA extends Record> {

    // TODO Update the JavaDocs

    /**
     * Checks whether this repository contains any aggregate roots.
     * <p>
     * Useful for determining if seed data needs to be loaded or if the
     * application is starting with an empty dataset.
     *
     * @return {@code true} if the repository is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Retrieves an aggregate root by its identifier.
     * <p>
     * Returns the complete aggregate with all its associated data and child objects.
     * If no aggregate exists with the given ID, returns an empty Optional.
     *
     * @param id the unique identifier of the aggregate to retrieve
     * @return an Optional containing the aggregate if found, empty otherwise
     */
    Optional<AR> findById(ID id);

    interface WithInsert<ID extends Identifier, AR extends AggregateRoot<ID, DATA>, DATA extends Record> extends Repository<ID, AR, DATA> {
        /**
         * Creates and persists a new aggregate root from the given business data.
         * <p>
         * The repository generates a new unique identifier and initializes the version
         * to 1, then persists the complete aggregate. The aggregate does not exist
         * until this method completes successfully.
         *
         * @param data the business data for the new aggregate
         * @return the generated identifier for the newly created aggregate
         * @throws org.springframework.dao.DataIntegrityViolationException if persistence constraints are violated
         */
        ID insert(DATA data);
    }

    interface WithUpdate<ID extends Identifier, AR extends AggregateRoot<ID, DATA>, DATA extends Record> extends Repository<ID, AR, DATA> {
        /**
         * Updates an existing aggregate root with modified data.
         * <p>
         * This method persists changes to an existing aggregate, incrementing its version
         * and verifying optimistic locking. If the aggregate's version doesn't match the
         * database version, the update fails with an exception, indicating concurrent
         * modification by another transaction.
         * <p>
         * Returns the updated aggregate with its new version number.
         *
         * @param aggregateRoot the aggregate to update, with modified data
         * @return the updated aggregate with incremented version
         * @throws org.springframework.dao.OptimisticLockingFailureException if the aggregate was modified concurrently
         */
        AR update(AR aggregateRoot);
    }

    interface WithDelete<ID extends Identifier, AR extends AggregateRoot<ID, DATA>, DATA extends Record> extends Repository<ID, AR, DATA> {
        /**
         * Deletes an aggregate root by its identifier.
         * <p>
         * Removes the aggregate and all its associated data from persistence.
         * If no aggregate exists with the given ID, this method has no effect.
         * <p>
         * <strong>Note:</strong> Consider carefully before implementing cascading deletes.
         * In many domains, aggregates should be marked as inactive rather than deleted
         * to preserve audit trails and historical data.
         *
         * @param id the identifier of the aggregate to delete
         */
        void deleteById(ID id);
    }
}
