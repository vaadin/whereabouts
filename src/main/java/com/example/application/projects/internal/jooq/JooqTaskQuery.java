package com.example.application.projects.internal.jooq;

import com.example.application.humanresources.EmployeeId;
import com.example.application.projects.TaskAssignee;
import com.example.application.projects.internal.TaskQuery;
import org.jooq.DSLContext;
import org.jooq.Records;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static com.example.application.jooq.Tables.EMPLOYEE;
import static com.example.application.projects.internal.jooq.JooqConverters.emailConverter;

@Component
@NullMarked
class JooqTaskQuery implements TaskQuery {

    private final DSLContext dsl;

    JooqTaskQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Stream<TaskAssignee> findAssigneesBySearchTerm(@Nullable String searchTerm, int limit, int offset) {
        var condition = (searchTerm != null && !searchTerm.isBlank())
                ? EMPLOYEE.FIRST_NAME.containsIgnoreCase(searchTerm)
                .or(EMPLOYEE.LAST_NAME.containsIgnoreCase(searchTerm))
                : DSL.trueCondition();
        var EMAIL = EMPLOYEE.WORK_EMAIL.convert(emailConverter);
        return dsl.select(
                        EMPLOYEE.EMPLOYEE_ID.convertFrom(EmployeeId::of),
                        EMPLOYEE.FIRST_NAME,
                        EMPLOYEE.LAST_NAME,
                        EMAIL
                )
                .from(EMPLOYEE)
                .where(condition)
                .orderBy(EMPLOYEE.FIRST_NAME.asc(), EMPLOYEE.LAST_NAME.asc())
                .fetch(Records.mapping(TaskAssignee::new))
                .stream();
    }
}
