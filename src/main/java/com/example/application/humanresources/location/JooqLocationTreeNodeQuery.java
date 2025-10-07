package com.example.application.humanresources.location;

import com.example.application.common.Country;
import com.example.application.common.address.PostalAddress;
import com.example.application.jooq.enums.EmploymentStatus;
import org.jooq.DSLContext;
import org.jooq.Record5;
import org.jooq.Records;
import org.jooq.SelectOnConditionStep;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.application.humanresources.location.JooqLocationConverters.locationTypeConverter;
import static com.example.application.humanresources.location.JooqLocationConverters.postalAddressConverter;
import static com.example.application.jooq.Tables.EMPLOYMENT;
import static com.example.application.jooq.Tables.LOCATION;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

@Component
@NullMarked
class JooqLocationTreeNodeQuery implements LocationTreeNodeQuery {
    private final DSLContext dsl;

    JooqLocationTreeNodeQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public int countCountriesWithLocations() {
        var count = countDistinct(LOCATION.COUNTRY);
        return requireNonNull(dsl.select(count).from(LOCATION).fetchOne(count));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public int countLocationsInCountry(Country country) {
        var count = count(LOCATION.COUNTRY);
        return requireNonNull(dsl.select(count).from(LOCATION).where(LOCATION.COUNTRY.eq(country.isoCode())).fetchOne(count));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<LocationTreeNode> findCountries(Pageable pageable) {
        var country = LOCATION.COUNTRY.convertFrom(Country::ofIsoCode);
        return dsl.select(
                        country,
                        count(EMPLOYMENT.EMPLOYEE_ID)
                )
                .from(LOCATION)
                .leftJoin(EMPLOYMENT).on(EMPLOYMENT.LOCATION_ID.eq(LOCATION.LOCATION_ID)
                        .and(EMPLOYMENT.EMPLOYMENT_STATUS.eq(EmploymentStatus.ACTIVE)))
                .groupBy(country)
                .orderBy(country) // TODO Sort from pageable
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(LocationTreeNode.CountryNode::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<LocationTreeNode> findLocations(Country country, Pageable pageable) {
        return selectLocation()
                .where(LOCATION.COUNTRY.eq(country.isoCode()))
                .groupBy(LOCATION.LOCATION_ID)
                .orderBy(LOCATION.NAME) // TODO Sort from pageable
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(LocationTreeNode.LocationNode::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public Optional<LocationTreeNode.LocationNode> findLocationById(LocationId locationId) {
        return selectLocation()
                .where(LOCATION.LOCATION_ID.eq(locationId.toLong()))
                .groupBy(LOCATION.LOCATION_ID)
                .fetchOptional(Records.mapping(LocationTreeNode.LocationNode::new));
    }

    private SelectOnConditionStep<Record5<LocationId, String, Integer, LocationType, PostalAddress>> selectLocation() {
        return dsl.select(
                        LOCATION.LOCATION_ID.convertFrom(LocationId::of),
                        LOCATION.NAME,
                        count(EMPLOYMENT.EMPLOYEE_ID),
                        LOCATION.LOCATION_TYPE.convert(locationTypeConverter),
                        LOCATION.ADDRESS.convert(postalAddressConverter))
                .from(LOCATION)
                .leftJoin(EMPLOYMENT).on(EMPLOYMENT.LOCATION_ID.eq(LOCATION.LOCATION_ID)
                        .and(EMPLOYMENT.EMPLOYMENT_STATUS.eq(EmploymentStatus.ACTIVE)));
    }
}
