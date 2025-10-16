package com.example.application.humanresources.internal.jooq;

import com.example.application.humanresources.Employee;
import com.example.application.humanresources.EmployeeData;
import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.internal.EmployeeRepository;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.application.humanresources.internal.jooq.JooqConverters.*;
import static com.example.application.jooq.Sequences.EMPLOYEE_ID_SEQ;
import static com.example.application.jooq.Tables.EMPLOYEE;

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
        var GENDER = EMPLOYEE.GENDER.convert(genderConverter);
        var HOME_ADDRESS = EMPLOYEE.HOME_ADDRESS.convert(postalAddressConverter);
        var TIME_ZONE = EMPLOYEE.TIME_ZONE.convert(zoneIdConverter);
        var WORK_PHONE = EMPLOYEE.WORK_PHONE.convert(phoneNumberConverter);
        var MOBILE_PHONE = EMPLOYEE.MOBILE_PHONE.convert(phoneNumberConverter);
        var HOME_PHONE = EMPLOYEE.HOME_PHONE.convert(phoneNumberConverter);
        var WORK_EMAIL = EMPLOYEE.WORK_EMAIL.convert(emailConverter);
        return dsl
                .select(EMPLOYEE.EMPLOYEE_ID,
                        EMPLOYEE.VERSION,
                        EMPLOYEE.FIRST_NAME,
                        EMPLOYEE.MIDDLE_NAME,
                        EMPLOYEE.LAST_NAME,
                        EMPLOYEE.PREFERRED_NAME,
                        EMPLOYEE.BIRTH_DATE,
                        GENDER,
                        EMPLOYEE.DIETARY_NOTES,
                        TIME_ZONE,
                        HOME_ADDRESS,
                        WORK_PHONE,
                        MOBILE_PHONE,
                        HOME_PHONE,
                        WORK_EMAIL
                )
                .from(EMPLOYEE)
                .where(EMPLOYEE.EMPLOYEE_ID.eq(id.toLong()))
                .fetchOptional(record -> new Employee(
                        EmployeeId.of(record.getValue(EMPLOYEE.EMPLOYEE_ID)),
                        record.getValue(EMPLOYEE.VERSION),
                        new EmployeeData(
                                record.getValue(EMPLOYEE.FIRST_NAME),
                                record.getValue(EMPLOYEE.MIDDLE_NAME),
                                record.getValue(EMPLOYEE.LAST_NAME),
                                record.getValue(EMPLOYEE.PREFERRED_NAME),
                                record.getValue(EMPLOYEE.BIRTH_DATE),
                                record.getValue(GENDER),
                                record.getValue(EMPLOYEE.DIETARY_NOTES),
                                record.getValue(TIME_ZONE),
                                record.getValue(HOME_ADDRESS),
                                record.getValue(WORK_PHONE),
                                record.getValue(MOBILE_PHONE),
                                record.getValue(HOME_PHONE),
                                record.getValue(WORK_EMAIL)
                        )
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public EmployeeId insert(EmployeeData employeeData) {
        var id = EmployeeId.of(dsl.nextval(EMPLOYEE_ID_SEQ));
        dsl.insertInto(EMPLOYEE)
                .set(EMPLOYEE.EMPLOYEE_ID, id.toLong())
                .set(EMPLOYEE.VERSION, 1L)
                .set(EMPLOYEE.FIRST_NAME, employeeData.firstName())
                .set(EMPLOYEE.MIDDLE_NAME, employeeData.middleName())
                .set(EMPLOYEE.LAST_NAME, employeeData.lastName())
                .set(EMPLOYEE.PREFERRED_NAME, employeeData.preferredName())
                .set(EMPLOYEE.BIRTH_DATE, employeeData.birthDate())
                .set(EMPLOYEE.GENDER, genderConverter.to(employeeData.gender()))
                .set(EMPLOYEE.DIETARY_NOTES, employeeData.dietaryNotes())
                .set(EMPLOYEE.TIME_ZONE, zoneIdConverter.to(employeeData.timeZone()))
                .set(EMPLOYEE.COUNTRY, employeeData.homeAddress().country().isoCode())
                .set(EMPLOYEE.HOME_ADDRESS, postalAddressConverter.to(employeeData.homeAddress()))
                .set(EMPLOYEE.WORK_PHONE, phoneNumberConverter.to(employeeData.workPhone()))
                .set(EMPLOYEE.MOBILE_PHONE, phoneNumberConverter.to(employeeData.mobilePhone()))
                .set(EMPLOYEE.HOME_PHONE, phoneNumberConverter.to(employeeData.homePhone()))
                .set(EMPLOYEE.WORK_EMAIL, emailConverter.to(employeeData.workEmail()))
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
                .set(EMPLOYEE.GENDER, genderConverter.to(employee.data().gender()))
                .set(EMPLOYEE.DIETARY_NOTES, employee.data().dietaryNotes())
                .set(EMPLOYEE.TIME_ZONE, zoneIdConverter.to(employee.data().timeZone()))
                .set(EMPLOYEE.HOME_ADDRESS, postalAddressConverter.to(employee.data().homeAddress()))
                .set(EMPLOYEE.WORK_PHONE, phoneNumberConverter.to(employee.data().workPhone()))
                .set(EMPLOYEE.MOBILE_PHONE, phoneNumberConverter.to(employee.data().mobilePhone()))
                .set(EMPLOYEE.HOME_PHONE, phoneNumberConverter.to(employee.data().homePhone()))
                .set(EMPLOYEE.WORK_EMAIL, emailConverter.to(employee.data().workEmail()))
                .where(EMPLOYEE.EMPLOYEE_ID.eq(employee.id().toLong()).and(EMPLOYEE.VERSION.eq(employee.version())))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Employee was modified by another user");
        }

        return new Employee(employee.id(), newVersion, employee.data());
    }
}
