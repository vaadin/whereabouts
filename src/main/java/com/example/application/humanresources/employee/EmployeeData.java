package com.example.application.humanresources.employee;

import com.example.application.common.EmailAddress;
import com.example.application.common.Gender;
import com.example.application.common.PhoneNumber;
import com.example.application.common.ValueObject;
import com.example.application.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;

@NullMarked
public record EmployeeData(
        String firstName,
        @Nullable String middleName,
        String lastName,
        String preferredName,
        LocalDate birthDate,
        Gender gender,
        @Nullable String dietaryNotes,
        ZoneId timeZone,
        PostalAddress homeAddress,
        @Nullable PhoneNumber workPhone,
        @Nullable PhoneNumber mobilePhone,
        @Nullable PhoneNumber homePhone,
        EmailAddress workEmail
) implements ValueObject {
    public static final String PROP_FIRST_NAME = "firstName";
    public static final String PROP_MIDDLE_NAME = "middleName";
    public static final String PROP_LAST_NAME = "lastName";
    public static final String PROP_PREFERRED_NAME = "preferredName";
    public static final String PROP_BIRTH_DATE = "birthDate";
    public static final String PROP_GENDER = "gender";
    public static final String PROP_DIETARY_NOTES = "dietaryNotes";
    public static final String PROP_TIME_ZONE = "timeZone";
    public static final String PROP_HOME_ADDRESS = "homeAddress";
    public static final String PROP_WORK_PHONE = "workPhone";
    public static final String PROP_MOBILE_PHONE = "mobilePhone";
    public static final String PROP_HOME_PHONE = "homePhone";
    public static final String PROP_WORK_EMAIL = "workEmail";
}
