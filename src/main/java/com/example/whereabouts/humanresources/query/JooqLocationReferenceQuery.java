package com.example.whereabouts.humanresources.query;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.humanresources.LocationId;
import com.example.whereabouts.humanresources.LocationReference;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.example.whereabouts.jooq.Tables.LOCATION;

/**
 * @see "Design Decision: DD009-20251029-jooq-user-types.md"
 */
@Component
@NullMarked
class JooqLocationReferenceQuery implements LocationReferenceQuery {

    private final DSLContext dsl;

    JooqLocationReferenceQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

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

    @Override
    public Set<LocationReference> findByIds(Set<LocationId> ids) {
        return selectLocation()
                .where(LOCATION.LOCATION_ID.in(ids))
                .fetchSet(Records.mapping(LocationReference::new));
    }

    private SelectJoinStep<Record3<LocationId, String, Country>> selectLocation() {
        return dsl.select(
                        LOCATION.LOCATION_ID,
                        LOCATION.NAME,
                        LOCATION.COUNTRY)
                .from(LOCATION);
    }
}
