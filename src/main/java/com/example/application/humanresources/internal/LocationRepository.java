package com.example.application.humanresources.internal;

import com.example.application.common.Repository;
import com.example.application.humanresources.Location;
import com.example.application.humanresources.LocationData;
import com.example.application.humanresources.LocationId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface LocationRepository extends Repository {

    Optional<Location> findById(LocationId id);

    LocationId insert(LocationData data);

    Location update(Location location);

    void deleteById(LocationId id);
}
