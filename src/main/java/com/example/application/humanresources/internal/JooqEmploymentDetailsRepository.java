package com.example.application.humanresources.internal;

import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmploymentDetails;
import com.example.application.humanresources.EmploymentDetailsData;
import com.example.application.humanresources.LocationId;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.application.humanresources.internal.JooqConverters.*;
import static com.example.application.jooq.Tables.EMPLOYMENT_DETAILS;

@Component
@NullMarked
class JooqEmploymentDetailsRepository implements EmploymentDetailsRepository {

    private final DSLContext dsl;

    JooqEmploymentDetailsRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public boolean isEmpty() {
        return dsl.selectCount().from(EMPLOYMENT_DETAILS).fetchSingle().value1() == 0;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public Optional<EmploymentDetails> findById(EmployeeId id) {
        var EMPLOYMENT_TYPE = EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE.convert(employmentTypeConverter);
        var EMPLOYMENT_STATUS = EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS.convert(employmentStatusConverter);
        var WORK_ARRANGEMENT = EMPLOYMENT_DETAILS.WORK_ARRANGEMENT.convert(workArrangementConverter);
        return dsl
                .select(EMPLOYMENT_DETAILS.EMPLOYEE_ID,
                        EMPLOYMENT_DETAILS.VERSION,
                        EMPLOYMENT_DETAILS.JOB_TITLE,
                        EMPLOYMENT_TYPE,
                        EMPLOYMENT_STATUS,
                        WORK_ARRANGEMENT,
                        EMPLOYMENT_DETAILS.LOCATION_ID,
                        EMPLOYMENT_DETAILS.HIRE_DATE,
                        EMPLOYMENT_DETAILS.TERMINATION_DATE
                )
                .from(EMPLOYMENT_DETAILS)
                .where(EMPLOYMENT_DETAILS.EMPLOYEE_ID.eq(id.toLong()))
                .fetchOptional(record -> new EmploymentDetails(
                        EmployeeId.of(record.getValue(EMPLOYMENT_DETAILS.EMPLOYEE_ID)),
                        record.getValue(EMPLOYMENT_DETAILS.VERSION),
                        new EmploymentDetailsData(
                                record.getValue(EMPLOYMENT_DETAILS.JOB_TITLE),
                                record.getValue(EMPLOYMENT_TYPE),
                                record.getValue(EMPLOYMENT_STATUS),
                                record.getValue(WORK_ARRANGEMENT),
                                LocationId.of(record.getValue(EMPLOYMENT_DETAILS.LOCATION_ID)),
                                record.getValue(EMPLOYMENT_DETAILS.HIRE_DATE),
                                record.getValue(EMPLOYMENT_DETAILS.TERMINATION_DATE)
                        )
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public EmploymentDetails insert(EmployeeId id, EmploymentDetailsData data) {
        dsl.insertInto(EMPLOYMENT_DETAILS)
                .set(EMPLOYMENT_DETAILS.EMPLOYEE_ID, id.toLong())
                .set(EMPLOYMENT_DETAILS.VERSION, 1L)
                .set(EMPLOYMENT_DETAILS.JOB_TITLE, data.jobTitle())
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE, employmentTypeConverter.to(data.type()))
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS, employmentStatusConverter.to(data.status()))
                .set(EMPLOYMENT_DETAILS.WORK_ARRANGEMENT, workArrangementConverter.to(data.workArrangement()))
                .set(EMPLOYMENT_DETAILS.LOCATION_ID, data.location().toLong())
                .set(EMPLOYMENT_DETAILS.HIRE_DATE, data.hireDate())
                .set(EMPLOYMENT_DETAILS.TERMINATION_DATE, data.terminationDate())
                .execute();
        return new EmploymentDetails(id, 1L, data);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public EmploymentDetails update(EmploymentDetails employmentDetails) {
        var newVersion = employmentDetails.version() + 1;
        var rowsUpdated = dsl.update(EMPLOYMENT_DETAILS)
                .set(EMPLOYMENT_DETAILS.VERSION, newVersion)
                .set(EMPLOYMENT_DETAILS.JOB_TITLE, employmentDetails.data().jobTitle())
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_TYPE, employmentTypeConverter.to(employmentDetails.data().type()))
                .set(EMPLOYMENT_DETAILS.EMPLOYMENT_STATUS, employmentStatusConverter.to(employmentDetails.data().status()))
                .set(EMPLOYMENT_DETAILS.WORK_ARRANGEMENT, workArrangementConverter.to(employmentDetails.data().workArrangement()))
                .set(EMPLOYMENT_DETAILS.LOCATION_ID, employmentDetails.data().location().toLong())
                .set(EMPLOYMENT_DETAILS.HIRE_DATE, employmentDetails.data().hireDate())
                .set(EMPLOYMENT_DETAILS.TERMINATION_DATE, employmentDetails.data().terminationDate())
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Employment was modified by another user");
        }

        return new EmploymentDetails(employmentDetails.id(), newVersion, employmentDetails.data());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteById(EmployeeId id) {
        dsl.delete(EMPLOYMENT_DETAILS)
                .where(EMPLOYMENT_DETAILS.EMPLOYEE_ID.eq(id.toLong()))
                .execute();
    }
}
