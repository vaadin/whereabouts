package com.example.application.humanresources;

import com.example.application.common.Country;
import com.example.application.common.address.PostalAddressConverter;
import com.example.application.jooq.enums.EmploymentStatus;
import org.jooq.DSLContext;
import org.jooq.Records;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.application.jooq.Tables.EMPLOYMENT;
import static com.example.application.jooq.Tables.LOCATION;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

@Service
@PreAuthorize("isAuthenticated()")
@NullMarked
public class LocationService {

    private final DSLContext dsl;

    LocationService(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(readOnly = true)
    public int countCountriesWithLocations() {
        var count = countDistinct(LOCATION.COUNTRY);
        return requireNonNull(dsl.select(count).from(LOCATION).fetchOne(count));
    }

    @Transactional(readOnly = true)
    public int countLocationsInCountry(Country country) {
        var count = count(LOCATION.COUNTRY);
        return requireNonNull(dsl.select(count).from(LOCATION).where(LOCATION.COUNTRY.eq(country.isoCode())).fetchOne(count));
    }

    @Transactional(readOnly = true)
    public List<LocationTreeNode.CountryNode> findCountries(Pageable pageable) {
        var country = LOCATION.COUNTRY.convertFrom(Country::ofIsoCode);
        return dsl.select(
                        country,
                        count(EMPLOYMENT.EMPLOYEE_ID)
                )
                .from(LOCATION)
                .leftJoin(EMPLOYMENT).on(EMPLOYMENT.PRIMARY_LOCATION_ID.eq(LOCATION.LOCATION_ID).and(EMPLOYMENT.STATUS.eq(EmploymentStatus.active)))
                .groupBy(country)
                .orderBy(country) // TODO Sort from pageable
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(LocationTreeNode.CountryNode::new));
    }

    @Transactional(readOnly = true)
    public List<LocationTreeNode.LocationNode> findLocations(Country country, Pageable pageable) {
        return dsl.select(
                        LOCATION.LOCATION_ID.convertFrom(LocationId::of),
                        LOCATION.NAME,
                        count(EMPLOYMENT.EMPLOYEE_ID),
                        LOCATION.LOCATION_TYPE.convertFrom(type -> LocationType.valueOf(type.name().toUpperCase())),
                        LOCATION.ADDRESS.convert(PostalAddressConverter.instance()))
                .from(LOCATION)
                .leftJoin(EMPLOYMENT).on(EMPLOYMENT.PRIMARY_LOCATION_ID.eq(LOCATION.LOCATION_ID).and(EMPLOYMENT.STATUS.eq(EmploymentStatus.active)))
                .where(LOCATION.COUNTRY.eq(country.isoCode()))
                .groupBy(LOCATION.LOCATION_ID)
                .orderBy(LOCATION.NAME) // TODO Sort from pageable
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(LocationTreeNode.LocationNode::new));
    }

    public Optional<LocationTreeNode.LocationNode> findLocationById(LocationId locationId) {
        return Optional.empty();
    }
}
