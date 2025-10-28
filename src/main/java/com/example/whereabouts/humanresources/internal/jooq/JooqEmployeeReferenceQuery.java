package com.example.whereabouts.humanresources.internal.jooq;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.humanresources.*;
import com.example.whereabouts.humanresources.internal.EmployeeReferenceQuery;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.example.whereabouts.humanresources.internal.jooq.JooqConverters.employmentTypeConverter;
import static com.example.whereabouts.jooq.Tables.EMPLOYEE;
import static com.example.whereabouts.jooq.Tables.EMPLOYMENT_DETAILS;

@Component
@NullMarked
class JooqEmployeeReferenceQuery implements EmployeeReferenceQuery {

    private static final Field<EmploymentType> EMPLOYMENT_TYPE = EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE.convert(employmentTypeConverter);
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, EmployeeSortableProperty.LAST_NAME.name(),
            EmployeeSortableProperty.FIRST_NAME.name());
    private final DSLContext dsl;

    JooqEmployeeReferenceQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<EmployeeReference> findByFilter(Pageable pageable, EmployeeFilter filter) {
        Condition condition = DSL.trueCondition();
        if (filter.searchTerm() != null && !filter.searchTerm().isBlank()) {
            // TODO Should the search term be split by spaces and used individually?
            //  Searching for a first and last name would now result in no hits.
            condition = condition.and(EMPLOYEE.FIRST_NAME.containsIgnoreCase(filter.searchTerm())
                    .or(EMPLOYEE.LAST_NAME.containsIgnoreCase(filter.searchTerm())));
        }
        if (!filter.statuses().isEmpty()) {
            condition = condition.and(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS.in(filter.statuses()));
        }
        if (!filter.types().isEmpty()) {
            condition = condition.and(EMPLOYMENT_TYPE.in(filter.types()));
        }
        return selectEmployee()
                .where(condition)
                .orderBy(toOrderFields(pageable.getSortOr(DEFAULT_SORT)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(EmployeeReference::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public Set<EmployeeReference> findByIds(Set<EmployeeId> ids) {
        return selectEmployee()
                .where(EMPLOYEE.EMPLOYEE_ID.in(ids))
                .fetchSet(Records.mapping(EmployeeReference::new));
    }

    private SelectOnConditionStep<Record6<EmployeeId, String, String, String, Country, String>> selectEmployee() {
        return dsl.select(
                        EMPLOYEE.EMPLOYEE_ID,
                        EMPLOYEE.FIRST_NAME,
                        EMPLOYEE.MIDDLE_NAME,
                        EMPLOYEE.LAST_NAME,
                        EMPLOYEE.COUNTRY,
                        EMPLOYMENT_DETAILS.JOB_TITLE)
                .from(EMPLOYEE)
                .leftJoin(EMPLOYMENT_DETAILS).on(EMPLOYMENT_DETAILS.EMPLOYEE_ID.eq(EMPLOYEE.EMPLOYEE_ID));
    }

    private List<? extends OrderField<?>> toOrderFields(Sort sort) {
        return sort.stream().map(this::toOrderField).toList();
    }

    private OrderField<?> toOrderField(Sort.Order order) {
        var property = EmployeeSortableProperty.valueOf(order.getProperty());
        return switch (property) {
            case FIRST_NAME -> order.isAscending() ? EMPLOYEE.FIRST_NAME.asc() : EMPLOYEE.FIRST_NAME.desc();
            case LAST_NAME -> order.isAscending() ? EMPLOYEE.LAST_NAME.asc() : EMPLOYEE.LAST_NAME.desc();
        };
    }
}
