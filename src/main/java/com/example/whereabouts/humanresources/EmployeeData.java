package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.EmailAddress;
import com.example.whereabouts.common.Gender;
import com.example.whereabouts.common.PhoneNumber;
import com.example.whereabouts.common.ValueObject;
import com.example.whereabouts.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
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
