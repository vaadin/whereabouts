package com.example.whereabouts.humanresources.internal.jooq;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.EmailAddress;
import com.example.whereabouts.common.Gender;
import com.example.whereabouts.common.PhoneNumber;
import com.example.whereabouts.humanresources.*;
import com.example.whereabouts.jooq.converters.*;
import org.jooq.Converter;

import java.time.ZoneId;

@Deprecated
final class JooqConverters {

    private JooqConverters() {
    }

    @Deprecated
    public static final Converter<com.example.whereabouts.jooq.enums.EmploymentStatus, EmploymentStatus> employmentStatusConverter = new EmploymentStatusConverter();

    @Deprecated
    public static final Converter<com.example.whereabouts.jooq.enums.EmploymentType, EmploymentType> employmentTypeConverter = new EmploymentTypeConverter();

    @Deprecated
    public static final Converter<com.example.whereabouts.jooq.enums.WorkArrangement, WorkArrangement> workArrangementConverter = new WorkArrangementConverter();

    @Deprecated
    public static final Converter<com.example.whereabouts.jooq.enums.Gender, Gender> genderConverter = new GenderConverter();

    @Deprecated
    public static final Converter<com.example.whereabouts.jooq.enums.LocationType, LocationType> locationTypeConverter = new LocationTypeConverter();

    @Deprecated
    public static final PostalAddressConverter postalAddressConverter = new PostalAddressConverter();

    @Deprecated
    public static final Converter<String, ZoneId> zoneIdConverter = new ZoneIdConverter();

    @Deprecated
    public static final Converter<String, PhoneNumber> phoneNumberConverter = new PhoneNumberConverter();

    @Deprecated
    public static final Converter<String, EmailAddress> emailConverter = new EmailConverter();

    @Deprecated
    public static final Converter<Long, LocationId> locationIdConverter = new LocationIdConverter();

    @Deprecated
    public static final Converter<Long, EmployeeId> employeeIdConverter = new EmployeeIdConverter();

    @Deprecated
    public static final Converter<String, Country> countryConverter = new CountryConverter();
}
