package com.example.whereabouts.humanresources.query;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.humanresources.LocationId;
import com.example.whereabouts.humanresources.LocationTreeNode;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @see "Design Decision: DD009-20251029-jooq-user-types.md"
 */
@NullMarked
public interface LocationTreeNodeQuery {
    int countCountriesWithLocations();

    int countLocationsInCountry(Country country);

    List<LocationTreeNode> findCountries(Pageable pageable);

    List<LocationTreeNode> findLocations(Country country, Pageable pageable);

    Optional<LocationTreeNode.LocationNode> findLocationById(LocationId locationId);
}
