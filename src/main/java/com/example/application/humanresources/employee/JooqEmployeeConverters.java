package com.example.application.humanresources.employee;

import com.example.application.common.EmailAddress;
import com.example.application.common.Gender;
import com.example.application.common.PhoneNumber;
import com.example.application.common.address.JooqPostalAddressConverter;
import org.jooq.Converter;

import java.time.ZoneId;

final class JooqEmployeeConverters {

    private JooqEmployeeConverters() {
    }

    public static final Converter<com.example.application.jooq.enums.EmploymentStatus, EmploymentStatus> employmentStatusConverter = Converter.ofNullable(
            com.example.application.jooq.enums.EmploymentStatus.class,
            EmploymentStatus.class,
            dbType -> EmploymentStatus.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.EmploymentStatus.valueOf(domainType.name())
    );

    public static final Converter<com.example.application.jooq.enums.EmploymentType, EmploymentType> employmentTypeConverter = Converter.ofNullable(
            com.example.application.jooq.enums.EmploymentType.class,
            EmploymentType.class,
            dbType -> EmploymentType.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.EmploymentType.valueOf(domainType.name())
    );

    public static final Converter<com.example.application.jooq.enums.WorkArrangement, WorkArrangement> workArrangementConverter = Converter.ofNullable(
            com.example.application.jooq.enums.WorkArrangement.class,
            WorkArrangement.class,
            dbType -> WorkArrangement.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.WorkArrangement.valueOf(domainType.name())
    );

    public static final Converter<com.example.application.jooq.enums.Gender, Gender> genderConverter = Converter.ofNullable(
            com.example.application.jooq.enums.Gender.class,
            Gender.class,
            dbType -> Gender.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.Gender.valueOf(domainType.name())
    );

    public static final JooqPostalAddressConverter postalAddressConverter = new JooqPostalAddressConverter();

    public static final Converter<String, ZoneId> zoneIdConverter = Converter.ofNullable(
            String.class, ZoneId.class, ZoneId::of, ZoneId::getId
    );

    public static final Converter<String, PhoneNumber> phoneNumberConverter = Converter.ofNullable(
            String.class, PhoneNumber.class, PhoneNumber::of, PhoneNumber::toString
    );

    public static final Converter<String, EmailAddress> emailConverter = Converter.ofNullable(
            String.class, EmailAddress.class, EmailAddress::of, EmailAddress::toString
    );
}
