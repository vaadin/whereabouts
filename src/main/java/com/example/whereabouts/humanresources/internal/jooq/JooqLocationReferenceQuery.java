package com.example.whereabouts.humanresources.internal.jooq;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.humanresources.LocationId;
import com.example.whereabouts.humanresources.LocationReference;
import com.example.whereabouts.humanresources.internal.LocationReferenceQuery;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.example.whereabouts.humanresources.internal.jooq.JooqConverters.locationIdConverter;
import static com.example.whereabouts.jooq.Tables.LOCATION;

@Component
@NullMarked
class JooqLocationReferenceQuery implements LocationReferenceQuery {

    private static final Field<LocationId> LOCATION_ID = LOCATION.LOCATION_ID.convert(locationIdConverter);

    private final DSLContext dsl;

    JooqLocationReferenceQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<LocationReference> findBySearchTerm(Pageable pageable, @Nullable String searchTerm) {
        Condition condition = DSL.trueCondition();
        if (searchTerm != null && !searchTerm.isBlank()) {
            condition = LOCATION.NAME.containsIgnoreCase(searchTerm);
        }
        return selectLocation()
                .where(condition)
                .orderBy(LOCATION.NAME)
                .fetch(Records.mapping(LocationReference::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public Set<LocationReference> findByIds(Set<LocationId> ids) {
        return selectLocation()
                .where(LOCATION_ID.in(ids))
                .fetchSet(Records.mapping(LocationReference::new));
    }

    private SelectJoinStep<Record3<LocationId, String, Country>> selectLocation() {
        return dsl.select(
                        LOCATION_ID,
                        LOCATION.NAME,
                        LOCATION.COUNTRY)
                .from(LOCATION);
    }
}
