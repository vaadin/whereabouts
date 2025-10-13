package com.example.application.humanresources;

import com.example.application.humanresources.internal.LocationRepository;
import com.example.application.humanresources.internal.LocationTreeNodeQuery;
import com.example.application.security.AppRoles;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasRole('" + AppRoles.LOCATION_READ + "')")
@NullMarked
public class LocationService {

    public static final String SORT_BY_LOCATION = "location";
    public static final String SORT_BY_TYPE = "locationType";
    public static final String SORT_BY_EMPLOYEES = "employees";

    private final LocationRepository locationRepository;
    private final LocationTreeNodeQuery locationTreeNodeQuery;

    LocationService(LocationRepository locationRepository, LocationTreeNodeQuery locationTreeNodeQuery) {
        this.locationRepository = locationRepository;
        this.locationTreeNodeQuery = locationTreeNodeQuery;
    }

    @Transactional(readOnly = true)
    public int getChildCount(@Nullable LocationTreeNode node) {
        if (node == null) {
            return locationTreeNodeQuery.countCountriesWithLocations();
        } else if (node instanceof LocationTreeNode.CountryNode countryNode) {
            return locationTreeNodeQuery.countLocationsInCountry(countryNode.country());
        } else {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public boolean hasChildren(@Nullable LocationTreeNode node) {
        return node instanceof LocationTreeNode.CountryNode;
    }

    @Transactional(readOnly = true)
    public List<LocationTreeNode> getChildren(@Nullable LocationTreeNode node, Pageable pageable) {
        if (node == null) {
            return locationTreeNodeQuery.findCountries(pageable);
        } else if (node instanceof LocationTreeNode.CountryNode countryNode) {
            return locationTreeNodeQuery.findLocations(countryNode.country(), pageable);
        } else {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<LocationTreeNode.LocationNode> getLocationNodeById(LocationId locationId) {
        return locationTreeNodeQuery.findLocationById(locationId);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.LOCATION_CREATE + "')")
    public LocationId insert(LocationData locationData) {
        return locationRepository.insert(locationData);
    }

    @Transactional(readOnly = true)
    public Optional<Location> findById(LocationId locationId) {
        return locationRepository.findById(locationId);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.LOCATION_UPDATE + "')")
    public Location update(Location location) {
        return locationRepository.update(location);
    }
}
