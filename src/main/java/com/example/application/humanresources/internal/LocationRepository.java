package com.example.application.humanresources.internal;

import com.example.application.common.CrudRepository;
import com.example.application.humanresources.Location;
import com.example.application.humanresources.LocationData;
import com.example.application.humanresources.LocationId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface LocationRepository extends CrudRepository<LocationId, Location, LocationData> {
}
