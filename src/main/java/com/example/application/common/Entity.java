package com.example.application.common;

import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

/**
 * Marker interface for entities in Domain-Driven Design.
 *
 * @param <ID> the type of identifier used by this entity
 */
@NullMarked
public interface Entity<ID extends Identifier> extends Serializable {

    ID id();
}
