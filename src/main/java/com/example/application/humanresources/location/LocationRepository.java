package com.example.application.humanresources.location;

import com.example.application.common.Repository;
import org.jspecify.annotations.NullMarked;

/**
 * Repository interface for {@link Location} aggregate roots.
 * <p>
 * This repository provides persistence operations for office locations, including their
 * associated facilities. It follows the standard {@link Repository} contract for aggregate
 * roots, supporting creation, retrieval, updates, and deletion of complete location aggregates.
 * <p>
 * <strong>Package-Private by Design</strong><br>
 * This interface intentionally has package visibility rather than public access. In this
 * architecture, the domain model (entities, value objects, repositories) and application
 * services all reside in the same package. Application services act as the public API and
 * facade for the domain, orchestrating use cases and coordinating repository access.
 * <p>
 * Only application services and background jobs within this package should access repositories
 * directly. UI components in other packages interact exclusively through application services,
 * never directly with repositories. This enforces clear architectural boundaries:
 * <ul>
 *   <li><strong>UI Layer</strong> → calls application services</li>
 *   <li><strong>Application Services</strong> → orchestrate domain logic and use repositories</li>
 *   <li><strong>Repositories</strong> → handle persistence concerns</li>
 *   <li><strong>Domain Model</strong> → contains business logic and rules</li>
 * </ul>
 * <p>
 * This layering prevents UI code from bypassing business logic or transaction boundaries
 * by directly manipulating persistence. All domain operations flow through services that
 * can enforce security, manage transactions, and coordinate multiple repositories if needed.
 *
 * @see Location
 * @see LocationData
 * @see Repository
 */
@NullMarked
interface LocationRepository extends Repository<LocationId, Location, LocationData> {
}
