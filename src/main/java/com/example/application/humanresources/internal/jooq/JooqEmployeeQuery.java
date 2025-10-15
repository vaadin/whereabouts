package com.example.application.humanresources.internal.jooq;

import com.example.application.common.Country;
import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmployeeReference;
import com.example.application.humanresources.EmployeeSortableProperty;
import com.example.application.humanresources.internal.EmployeeQuery;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.example.application.humanresources.internal.jooq.JooqConverters.countryConverter;
import static com.example.application.humanresources.internal.jooq.JooqConverters.employeeIdConverter;
import static com.example.application.jooq.Tables.EMPLOYEE;
import static com.example.application.jooq.Tables.EMPLOYMENT_DETAILS;

@Component
@NullMarked
class JooqEmployeeQuery implements EmployeeQuery {

    private static final Field<EmployeeId> EMPLOYEE_ID = EMPLOYEE.EMPLOYEE_ID.convert(employeeIdConverter);
    private static final Field<EmployeeId> EMPLOYMENT_DETAILS_ID = EMPLOYMENT_DETAILS.EMPLOYEE_ID.convert(employeeIdConverter);
    private static final Field<Country> COUNTRY = EMPLOYEE.COUNTRY.convert(countryConverter);
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, EmployeeSortableProperty.LAST_NAME.name(),
            EmployeeSortableProperty.FIRST_NAME.name());
    private final DSLContext dsl;

    JooqEmployeeQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<EmployeeReference> findEmployees(Pageable pageable, @Nullable String searchTerm) {
        var condition = searchTerm != null
                ? EMPLOYEE.FIRST_NAME.containsIgnoreCase(searchTerm)
                .or(EMPLOYEE.LAST_NAME.containsIgnoreCase(searchTerm))
                : DSL.trueCondition();

        return selectEmployee()
                .where(condition)
                .orderBy(toOrderFields(pageable.getSortOr(DEFAULT_SORT)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(EmployeeReference::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public Set<EmployeeReference> findEmployeesByIds(Set<EmployeeId> ids) {
        return selectEmployee()
                .where(EMPLOYEE.EMPLOYEE_ID.in(ids))
                .fetchSet(Records.mapping(EmployeeReference::new));
    }

    private SelectOnConditionStep<Record6<EmployeeId, String, String, String, Country, String>> selectEmployee() {
        return dsl.select(
                        EMPLOYEE_ID,
                        EMPLOYEE.FIRST_NAME,
                        EMPLOYEE.MIDDLE_NAME,
                        EMPLOYEE.LAST_NAME,
                        COUNTRY,
                        EMPLOYMENT_DETAILS.JOB_TITLE)
                .from(EMPLOYEE)
                .leftJoin(EMPLOYMENT_DETAILS).on(EMPLOYMENT_DETAILS_ID.eq(EMPLOYEE_ID));
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
