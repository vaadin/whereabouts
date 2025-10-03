package com.example.application.common;

/**
 * Marker interface for entity identifiers in Domain-Driven Design.
 * <p>
 * Identifiers are special value objects that uniquely identify entities. While identifiers
 * are value objects (defined by their value, immutable), they serve the specific purpose
 * of entity identification and are therefore distinguished by this marker interface.
 * <p>
 * Using strongly-typed identifiers instead of primitives provides:
 * <ul>
 *   <li>Type safety - prevents mixing IDs from different entity types</li>
 *   <li>Self-documenting code - method signatures clearly indicate expected ID types</li>
 *   <li>Encapsulation - ID validation and formatting logic can be centralized</li>
 *   <li>Future flexibility - ID structure can evolve without breaking callers</li>
 * </ul>
 *
 * @see ValueObject
 * @see Entity
 */
public interface Identifier extends ValueObject {
}
