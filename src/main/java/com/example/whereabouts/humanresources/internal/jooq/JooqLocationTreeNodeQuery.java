package com.example.whereabouts.humanresources.internal.jooq;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.address.PostalAddress;
import com.example.whereabouts.humanresources.LocationId;
import com.example.whereabouts.humanresources.LocationSortableProperty;
import com.example.whereabouts.humanresources.LocationTreeNode;
import com.example.whereabouts.humanresources.LocationType;
import com.example.whereabouts.humanresources.internal.LocationTreeNodeQuery;
import com.example.whereabouts.jooq.enums.EmploymentStatus;
import org.jooq.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.whereabouts.humanresources.internal.jooq.JooqConverters.locationTypeConverter;
import static com.example.whereabouts.humanresources.internal.jooq.JooqConverters.postalAddressConverter;
import static com.example.whereabouts.jooq.Tables.EMPLOYMENT_DETAILS;
import static com.example.whereabouts.jooq.Tables.LOCATION;
import static java.util.Objects.requireNonNull;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.countDistinct;

@Component
@NullMarked
class JooqLocationTreeNodeQuery implements LocationTreeNodeQuery {

    private static final Field<Integer> EMPLOYEES = count(EMPLOYMENT_DETAILS.EMPLOYEE_ID);
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, LocationSortableProperty.NAME.name());

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
        return requireNonNull(dsl.select(count).from(LOCATION).where(LOCATION.COUNTRY.eq(country)).fetchOne(count));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<LocationTreeNode> findCountries(Pageable pageable) {
        return dsl.select(
                        LOCATION.COUNTRY,
                        EMPLOYEES
                )
                .from(LOCATION)
                .leftJoin(EMPLOYMENT_DETAILS).on(EMPLOYMENT_DETAILS.LOCATION_ID.eq(LOCATION.LOCATION_ID)
                        .and(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS.eq(EmploymentStatus.ACTIVE)))
                .groupBy(LOCATION.COUNTRY)
                .orderBy(toCountryNodeOrderFields(pageable.getSortOr(DEFAULT_SORT)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(LocationTreeNode.CountryNode::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<LocationTreeNode> findLocations(Country country, Pageable pageable) {
        return selectLocation()
                .where(LOCATION.COUNTRY.eq(country))
                .groupBy(LOCATION.LOCATION_ID)
                .orderBy(toLocationNodeOrderFields(pageable.getSortOr(DEFAULT_SORT)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(LocationTreeNode.LocationNode::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public Optional<LocationTreeNode.LocationNode> findLocationById(LocationId id) {
        return selectLocation()
                .where(LOCATION.LOCATION_ID.eq(id.value()))
                .groupBy(LOCATION.LOCATION_ID)
                .fetchOptional(Records.mapping(LocationTreeNode.LocationNode::new));
    }

    private SelectOnConditionStep<Record5<LocationId, String, Integer, LocationType, PostalAddress>> selectLocation() {
        return dsl.select(
                        LOCATION.LOCATION_ID.convertFrom(LocationId::new),
                        LOCATION.NAME,
                        EMPLOYEES,
                        LOCATION.LOCATION_TYPE.convert(locationTypeConverter),
                        LOCATION.ADDRESS.convert(postalAddressConverter))
                .from(LOCATION)
                .leftJoin(EMPLOYMENT_DETAILS).on(EMPLOYMENT_DETAILS.LOCATION_ID.eq(LOCATION.LOCATION_ID)
                        .and(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS.eq(EmploymentStatus.ACTIVE)));
    }

    private List<? extends OrderField<?>> toLocationNodeOrderFields(Sort sort) {
        return sort.stream().map(this::toLocationNodeOrderField).toList();
    }

    private OrderField<?> toLocationNodeOrderField(Sort.Order order) {
        var property = LocationSortableProperty.valueOf(order.getProperty());
        return switch (property) {
            case NAME -> order.isAscending() ? LOCATION.NAME.asc() : LOCATION.NAME.desc();
            case LOCATION_TYPE -> order.isAscending() ? LOCATION.LOCATION_TYPE.asc() : LOCATION.LOCATION_TYPE.desc();
            case EMPLOYEES -> order.isAscending() ? EMPLOYEES.asc() : EMPLOYEES.desc();
        };
    }

    private List<? extends OrderField<?>> toCountryNodeOrderFields(Sort sort) {
        return sort.stream().map(this::toCountryNodeOrderField).toList();
    }

    private OrderField<?> toCountryNodeOrderField(Sort.Order order) {
        var property = LocationSortableProperty.valueOf(order.getProperty());
        return switch (property) {
            case NAME, LOCATION_TYPE ->
                    order.isAscending() ? LOCATION.COUNTRY.asc() : LOCATION.COUNTRY.desc(); // TODO This sorts by ISO code, not by display name. It will look wrong in the UI.
            case EMPLOYEES -> order.isAscending() ? EMPLOYEES.asc() : EMPLOYEES.desc();
        };
    }
}
