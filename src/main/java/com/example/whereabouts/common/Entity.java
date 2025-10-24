package com.example.whereabouts.common;

import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

/**
 * Marker interface for entities in Domain-Driven Design.
 *
 * @param <ID> the type of identifier used by this entity
 * @see "Design decision: DD002-20251023-ddd-marker-interfaces.md"
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public interface Entity<ID extends Identifier> extends Serializable {

    ID id();
}
