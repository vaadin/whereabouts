package com.example.whereabouts.humanresources.repository;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.humanresources.Location;
import com.example.whereabouts.humanresources.LocationData;
import com.example.whereabouts.humanresources.LocationId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public interface LocationRepository extends Repository {

    Optional<Location> findById(LocationId id);

    LocationId insert(LocationData data);

    Location update(Location location);
}
