package com.example.application.humanresources.internal.jooq;

import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmployeeReference;
import com.example.application.humanresources.internal.EmployeeQuery;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.Records;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.application.jooq.Tables.EMPLOYEE;
import static com.example.application.jooq.Tables.EMPLOYMENT_DETAILS;

@Component
@NullMarked
class JooqEmployeeQuery implements EmployeeQuery {

    private final DSLContext dsl;

    JooqEmployeeQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public List<EmployeeReference> findEmployees(@Nullable String searchTerm, Pageable pageable) {
        var condition = searchTerm != null
                ? EMPLOYEE.FIRST_NAME.containsIgnoreCase(searchTerm)
                .or(EMPLOYEE.LAST_NAME.containsIgnoreCase(searchTerm))
                : DSL.trueCondition();
        return selectEmployee()
                .where(condition)
                .orderBy(EMPLOYEE.LAST_NAME.asc(), EMPLOYEE.FIRST_NAME.asc()) // TODO Sort from pageable
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(Records.mapping(EmployeeReference::new));
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public Optional<EmployeeReference> findEmployeeById(EmployeeId id) {
        return selectEmployee()
                .where(EMPLOYEE.EMPLOYEE_ID.eq(id.toLong()))
                .fetchOptional(Records.mapping(EmployeeReference::new));
    }

    private SelectOnConditionStep<Record4<EmployeeId, String, String, String>> selectEmployee() {
        return dsl.select(
                        EMPLOYEE.EMPLOYEE_ID.convertFrom(EmployeeId::of),
                        EMPLOYEE.FIRST_NAME,
                        EMPLOYEE.LAST_NAME,
                        EMPLOYMENT_DETAILS.JOB_TITLE)
                .from(EMPLOYEE)
                .leftJoin(EMPLOYMENT_DETAILS).on(EMPLOYMENT_DETAILS.EMPLOYEE_ID.eq(EMPLOYEE.EMPLOYEE_ID));
    }
}
