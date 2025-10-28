package com.example.whereabouts.humanresources.repository;

import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.humanresources.EmploymentDetails;
import com.example.whereabouts.humanresources.EmploymentDetailsData;
import org.jooq.DSLContext;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.whereabouts.jooq.Tables.EMPLOYMENT_DETAILS;

@Component
class JooqEmploymentDetailsRepository implements EmploymentDetailsRepository {

    private final DSLContext dsl;

    JooqEmploymentDetailsRepository(@NonNull DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public @NonNull Optional<EmploymentDetails> findById(@NonNull EmployeeId id) {
        return dsl
                .select(EMPLOYMENT_DETAILS.EMPLOYEE_ID,
                        EMPLOYMENT_DETAILS.VERSION,
                        EMPLOYMENT_DETAILS.JOB_TITLE,
                        EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE,
                        EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS,
                        EMPLOYMENT_DETAILS.WORK_ARRANGEMENT,
                        EMPLOYMENT_DETAILS.LOCATION_ID,
                        EMPLOYMENT_DETAILS.MANAGER_EMPLOYEE_ID,
                        EMPLOYMENT_DETAILS.HIRE_DATE,
                        EMPLOYMENT_DETAILS.TERMINATION_DATE
                )
                .from(EMPLOYMENT_DETAILS)
                .where(EMPLOYMENT_DETAILS.EMPLOYEE_ID.eq(id))
                .fetchOptional(record -> new EmploymentDetails(
                        record.getValue(EMPLOYMENT_DETAILS.EMPLOYEE_ID),
                        record.getValue(EMPLOYMENT_DETAILS.VERSION),
                        new EmploymentDetailsData(
                                record.getValue(EMPLOYMENT_DETAILS.JOB_TITLE),
                                record.getValue(EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE),
                                record.getValue(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS),
                                record.getValue(EMPLOYMENT_DETAILS.WORK_ARRANGEMENT),
                                record.getValue(EMPLOYMENT_DETAILS.LOCATION_ID),
                                record.getValue(EMPLOYMENT_DETAILS.MANAGER_EMPLOYEE_ID),
                                record.getValue(EMPLOYMENT_DETAILS.HIRE_DATE),
                                record.getValue(EMPLOYMENT_DETAILS.TERMINATION_DATE)
                        )
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public @NonNull EmploymentDetails insert(@NonNull EmployeeId id, @NonNull EmploymentDetailsData data) {
        dsl.insertInto(EMPLOYMENT_DETAILS)
                .set(EMPLOYMENT_DETAILS.EMPLOYEE_ID, id)
                .set(EMPLOYMENT_DETAILS.VERSION, 1L)
                .set(EMPLOYMENT_DETAILS.JOB_TITLE, data.jobTitle())
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE, data.type())
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS, data.status())
                .set(EMPLOYMENT_DETAILS.WORK_ARRANGEMENT, data.workArrangement())
                .set(EMPLOYMENT_DETAILS.LOCATION_ID, data.location())
                .set(EMPLOYMENT_DETAILS.MANAGER_EMPLOYEE_ID, data.manager())
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
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE, employmentDetails.data().type())
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS, employmentDetails.data().status())
                .set(EMPLOYMENT_DETAILS.WORK_ARRANGEMENT, employmentDetails.data().workArrangement())
                .set(EMPLOYMENT_DETAILS.LOCATION_ID, employmentDetails.data().location())
                .set(EMPLOYMENT_DETAILS.MANAGER_EMPLOYEE_ID, employmentDetails.data().manager())
                .set(EMPLOYMENT_DETAILS.HIRE_DATE, employmentDetails.data().hireDate())
                .set(EMPLOYMENT_DETAILS.TERMINATION_DATE, employmentDetails.data().terminationDate())
                .where(EMPLOYMENT_DETAILS.EMPLOYEE_ID.eq(employmentDetails.id()))
                .and(EMPLOYMENT_DETAILS.VERSION.eq(employmentDetails.version()))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Employment was modified by another user");
        }

        return new EmploymentDetails(employmentDetails.id(), newVersion, employmentDetails.data());
    }
}
