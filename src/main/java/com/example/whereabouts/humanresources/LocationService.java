package com.example.whereabouts.humanresources;

import com.example.whereabouts.humanresources.query.LocationReferenceQuery;
import com.example.whereabouts.humanresources.query.LocationTreeNodeQuery;
import com.example.whereabouts.humanresources.repository.LocationRepository;
import com.example.whereabouts.security.AppRoles;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@PreAuthorize("hasRole('" + AppRoles.LOCATION_READ + "')")
@NullMarked
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationTreeNodeQuery locationTreeNodeQuery;
    private final LocationReferenceQuery locationReferenceQuery;

    LocationService(LocationRepository locationRepository, LocationTreeNodeQuery locationTreeNodeQuery, LocationReferenceQuery locationReferenceQuery) {
        this.locationRepository = locationRepository;
        this.locationTreeNodeQuery = locationTreeNodeQuery;
        this.locationReferenceQuery = locationReferenceQuery;
    }

    @Transactional(readOnly = true)
    public int countChildren(@Nullable LocationTreeNode node) {
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
    public List<LocationTreeNode> findChildren(@Nullable LocationTreeNode node, Pageable pageable) {
        if (node == null) {
            return locationTreeNodeQuery.findCountries(pageable);
        } else if (node instanceof LocationTreeNode.CountryNode countryNode) {
            return locationTreeNodeQuery.findLocations(countryNode.country(), pageable);
        } else {
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<LocationTreeNode.LocationNode> findLocationNodeById(LocationId locationId) {
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

    @Transactional(readOnly = true)
    public List<LocationReference> findReferencesBySearchTerm(Pageable pageable, @Nullable String searchTerm) {
        return locationReferenceQuery.findBySearchTerm(pageable, searchTerm);
    }

    @Transactional(readOnly = true)
    public Optional<LocationReference> getReferenceById(LocationId id) {
        return locationReferenceQuery.findByIds(Set.of(id)).stream().findFirst();
    }
}
