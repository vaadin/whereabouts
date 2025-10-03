package com.example.application.humanresources.location;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@NullMarked
interface LocationTreeNodeQuery {
    int countCountriesWithLocations();

    int countLocationsInCountry(Country country);

    List<LocationTreeNode> findCountries(Pageable pageable);

    List<LocationTreeNode> findLocations(Country country, Pageable pageable);

    Optional<LocationTreeNode.LocationNode> findLocationById(LocationId locationId);
}
