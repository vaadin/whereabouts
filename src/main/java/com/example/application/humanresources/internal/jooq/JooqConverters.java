package com.example.application.humanresources.internal.jooq;

import com.example.application.common.Country;
import com.example.application.common.EmailAddress;
import com.example.application.common.Gender;
import com.example.application.common.PhoneNumber;
import com.example.application.common.address.JooqPostalAddressConverter;
import com.example.application.humanresources.*;
import org.jooq.Converter;

import java.time.ZoneId;

final class JooqConverters {

    private JooqConverters() {
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

    public static final Converter<com.example.application.jooq.enums.LocationType, LocationType> locationTypeConverter = Converter.ofNullable(
            com.example.application.jooq.enums.LocationType.class,
            LocationType.class,
            dbType -> LocationType.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.LocationType.valueOf(domainType.name())
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

    public static final Converter<Long, LocationId> locationIdConverter = Converter.ofNullable(
            Long.class, LocationId.class, LocationId::of, LocationId::toLong
    );

    public static final Converter<Long, EmployeeId> employeeIdConverter = Converter.ofNullable(
            Long.class, EmployeeId.class, EmployeeId::of, EmployeeId::toLong
    );

    public static final Converter<String, Country> countryConverter = Converter.ofNullable(
            String.class, Country.class, Country::ofIsoCode, Country::toString
    );
}
