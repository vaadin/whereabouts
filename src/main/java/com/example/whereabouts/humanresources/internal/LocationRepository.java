package com.example.whereabouts.humanresources.internal;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.humanresources.Location;
import com.example.whereabouts.humanresources.LocationData;
import com.example.whereabouts.humanresources.LocationId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface LocationRepository extends Repository {

    Optional<Location> findById(LocationId id);

    LocationId insert(LocationData data);

    Location update(Location location);
}
