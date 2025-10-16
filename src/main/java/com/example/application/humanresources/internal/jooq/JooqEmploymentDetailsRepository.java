package com.example.application.humanresources.internal.jooq;

import com.example.application.humanresources.*;
import com.example.application.humanresources.internal.EmploymentDetailsRepository;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.application.humanresources.internal.jooq.JooqConverters.*;
import static com.example.application.jooq.Tables.EMPLOYMENT_DETAILS;

@Component
class JooqEmploymentDetailsRepository implements EmploymentDetailsRepository {

    private static final Field<EmployeeId> EMPLOYEE_ID = EMPLOYMENT_DETAILS.EMPLOYEE_ID.convert(employeeIdConverter);
    private static final Field<EmployeeId> MANAGER_ID = EMPLOYMENT_DETAILS.MANAGER_ID.convert(employeeIdConverter);
    private static final Field<LocationId> LOCATION_ID = EMPLOYMENT_DETAILS.LOCATION_ID.convert(locationIdConverter);
    private static final Field<EmploymentType> EMPLOYMENT_TYPE = EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE.convert(employmentTypeConverter);
    private static final Field<EmploymentStatus> EMPLOYMENT_STATUS = EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS.convert(employmentStatusConverter);
    private static final Field<WorkArrangement> WORK_ARRANGEMENT = EMPLOYMENT_DETAILS.WORK_ARRANGEMENT.convert(workArrangementConverter);
    private final DSLContext dsl;

    JooqEmploymentDetailsRepository(@NonNull DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public @NonNull Optional<EmploymentDetails> findById(@NonNull EmployeeId id) {
        return dsl
                .select(EMPLOYEE_ID,
                        EMPLOYMENT_DETAILS.VERSION,
                        EMPLOYMENT_DETAILS.JOB_TITLE,
                        EMPLOYMENT_TYPE,
                        EMPLOYMENT_STATUS,
                        WORK_ARRANGEMENT,
                        LOCATION_ID,
                        MANAGER_ID,
                        EMPLOYMENT_DETAILS.HIRE_DATE,
                        EMPLOYMENT_DETAILS.TERMINATION_DATE
                )
                .from(EMPLOYMENT_DETAILS)
                .where(EMPLOYEE_ID.eq(id))
                .fetchOptional(record -> new EmploymentDetails(
                        record.getValue(EMPLOYEE_ID),
                        record.getValue(EMPLOYMENT_DETAILS.VERSION),
                        new EmploymentDetailsData(
                                record.getValue(EMPLOYMENT_DETAILS.JOB_TITLE),
                                record.getValue(EMPLOYMENT_TYPE),
                                record.getValue(EMPLOYMENT_STATUS),
                                record.getValue(WORK_ARRANGEMENT),
                                record.getValue(LOCATION_ID),
                                record.getValue(MANAGER_ID),
                                record.getValue(EMPLOYMENT_DETAILS.HIRE_DATE),
                                record.getValue(EMPLOYMENT_DETAILS.TERMINATION_DATE)
                        )
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public @NonNull EmploymentDetails insert(@NonNull EmployeeId id, @NonNull EmploymentDetailsData data) {
        dsl.insertInto(EMPLOYMENT_DETAILS)
                .set(EMPLOYEE_ID, id)
                .set(EMPLOYMENT_DETAILS.VERSION, 1L)
                .set(EMPLOYMENT_DETAILS.JOB_TITLE, data.jobTitle())
                .set(EMPLOYMENT_TYPE, data.type())
                .set(EMPLOYMENT_STATUS, data.status())
                .set(WORK_ARRANGEMENT, data.workArrangement())
                .set(LOCATION_ID, data.location())
                .set(MANAGER_ID, data.manager())
                .set(EMPLOYMENT_DETAILS.HIRE_DATE, data.hireDate())
                .set(EMPLOYMENT_DETAILS.TERMINATION_DATE, data.terminationDate())
                .execute();
        return new EmploymentDetails(id, 1L, data);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public @NonNull EmploymentDetails update(@NonNull EmploymentDetails employmentDetails) {
        var newVersion = employmentDetails.version() + 1;
        var rowsUpdated = dsl.update(EMPLOYMENT_DETAILS)
                .set(EMPLOYMENT_DETAILS.VERSION, newVersion)
                .set(EMPLOYMENT_DETAILS.JOB_TITLE, employmentDetails.data().jobTitle())
                .set(EMPLOYMENT_TYPE, employmentDetails.data().type())
                .set(EMPLOYMENT_STATUS, employmentDetails.data().status())
                .set(WORK_ARRANGEMENT, employmentDetails.data().workArrangement())
                .set(LOCATION_ID, employmentDetails.data().location())
                .set(MANAGER_ID, employmentDetails.data().manager())
                .set(EMPLOYMENT_DETAILS.HIRE_DATE, employmentDetails.data().hireDate())
                .set(EMPLOYMENT_DETAILS.TERMINATION_DATE, employmentDetails.data().terminationDate())
                .where(EMPLOYEE_ID.eq(employmentDetails.id()))
                .and(EMPLOYMENT_DETAILS.VERSION.eq(employmentDetails.version()))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Employment was modified by another user");
        }

        return new EmploymentDetails(employmentDetails.id(), newVersion, employmentDetails.data());
    }
}
