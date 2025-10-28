package com.example.whereabouts.humanresources.internal.jooq;

import com.example.whereabouts.humanresources.Employee;
import com.example.whereabouts.humanresources.EmployeeData;
import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.humanresources.internal.EmployeeRepository;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.whereabouts.jooq.Sequences.EMPLOYEE_ID_SEQ;
import static com.example.whereabouts.jooq.Tables.EMPLOYEE;

@Component
@NullMarked
class JooqEmployeeRepository implements EmployeeRepository {

    private final DSLContext dsl;

    JooqEmployeeRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return dsl
                .select(EMPLOYEE.EMPLOYEE_ID,
                        EMPLOYEE.VERSION,
                        EMPLOYEE.FIRST_NAME,
                        EMPLOYEE.MIDDLE_NAME,
                        EMPLOYEE.LAST_NAME,
                        EMPLOYEE.PREFERRED_NAME,
                        EMPLOYEE.BIRTH_DATE,
                        EMPLOYEE.GENDER,
                        EMPLOYEE.DIETARY_NOTES,
                        EMPLOYEE.TIME_ZONE,
                        EMPLOYEE.HOME_POSTAL_ADDRESS,
                        EMPLOYEE.WORK_PHONE,
                        EMPLOYEE.MOBILE_PHONE,
                        EMPLOYEE.HOME_PHONE,
                        EMPLOYEE.WORK_EMAIL
                )
                .from(EMPLOYEE)
                .where(EMPLOYEE.EMPLOYEE_ID.eq(id))
                .fetchOptional(record -> new Employee(
                        record.getValue(EMPLOYEE.EMPLOYEE_ID),
                        record.getValue(EMPLOYEE.VERSION),
                        new EmployeeData(
                                record.getValue(EMPLOYEE.FIRST_NAME),
                                record.getValue(EMPLOYEE.MIDDLE_NAME),
                                record.getValue(EMPLOYEE.LAST_NAME),
                                record.getValue(EMPLOYEE.PREFERRED_NAME),
                                record.getValue(EMPLOYEE.BIRTH_DATE),
                                record.getValue(EMPLOYEE.GENDER),
                                record.getValue(EMPLOYEE.DIETARY_NOTES),
                                record.getValue(EMPLOYEE.TIME_ZONE),
                                record.getValue(EMPLOYEE.HOME_POSTAL_ADDRESS),
                                record.getValue(EMPLOYEE.WORK_PHONE),
                                record.getValue(EMPLOYEE.MOBILE_PHONE),
                                record.getValue(EMPLOYEE.HOME_PHONE),
                                record.getValue(EMPLOYEE.WORK_EMAIL)
                        )
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public EmployeeId insert(EmployeeData employeeData) {
        var id = new EmployeeId(dsl.nextval(EMPLOYEE_ID_SEQ));
        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.EMPLOYEE_ID, id)
                .set(EMPLOYEE.VERSION, 1L)
                .set(EMPLOYEE.FIRST_NAME, employeeData.firstName())
                .set(EMPLOYEE.MIDDLE_NAME, employeeData.middleName())
                .set(EMPLOYEE.LAST_NAME, employeeData.lastName())
                .set(EMPLOYEE.PREFERRED_NAME, employeeData.preferredName())
                .set(EMPLOYEE.BIRTH_DATE, employeeData.birthDate())
                .set(EMPLOYEE.GENDER, employeeData.gender())
                .set(EMPLOYEE.DIETARY_NOTES, employeeData.dietaryNotes())
                .set(EMPLOYEE.TIME_ZONE, employeeData.timeZone())
                .set(EMPLOYEE.COUNTRY, employeeData.homeAddress().country())
                .set(EMPLOYEE.HOME_POSTAL_ADDRESS, employeeData.homeAddress())
                .set(EMPLOYEE.WORK_PHONE, employeeData.workPhone())
                .set(EMPLOYEE.MOBILE_PHONE, employeeData.mobilePhone())
                .set(EMPLOYEE.HOME_PHONE, employeeData.homePhone())
                .set(EMPLOYEE.WORK_EMAIL, employeeData.workEmail())
                .execute();
        return id;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Employee update(Employee employee) {
        var newVersion = employee.version() + 1;
        var rowsUpdated = dsl.update(EMPLOYEE)
                .set(EMPLOYEE.VERSION, newVersion)
                .set(EMPLOYEE.FIRST_NAME, employee.data().firstName())
                .set(EMPLOYEE.MIDDLE_NAME, employee.data().middleName())
                .set(EMPLOYEE.LAST_NAME, employee.data().lastName())
                .set(EMPLOYEE.PREFERRED_NAME, employee.data().preferredName())
                .set(EMPLOYEE.BIRTH_DATE, employee.data().birthDate())
                .set(EMPLOYEE.GENDER, employee.data().gender())
                .set(EMPLOYEE.DIETARY_NOTES, employee.data().dietaryNotes())
                .set(EMPLOYEE.TIME_ZONE, employee.data().timeZone())
                .set(EMPLOYEE.HOME_POSTAL_ADDRESS, employee.data().homeAddress())
                .set(EMPLOYEE.WORK_PHONE, employee.data().workPhone())
                .set(EMPLOYEE.MOBILE_PHONE, employee.data().mobilePhone())
                .set(EMPLOYEE.HOME_PHONE, employee.data().homePhone())
                .set(EMPLOYEE.WORK_EMAIL, employee.data().workEmail())
                .where(EMPLOYEE.EMPLOYEE_ID.eq(employee.id()).and(EMPLOYEE.VERSION.eq(employee.version())))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Employee was modified by another user");
        }

        return new Employee(employee.id(), newVersion, employee.data());
    }
}
