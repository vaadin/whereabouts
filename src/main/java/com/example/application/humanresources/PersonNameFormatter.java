package com.example.application.humanresources;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class PersonNameFormatter {

    private static final PersonNameFormatter INSTANCE = new PersonNameFormatter();

    private PersonNameFormatter() {
    }

    public static PersonNameFormatter firstLast() {
        return INSTANCE;
    }

    public String toFullName(@Nullable String firstName, @Nullable String middleName, @Nullable String lastName) {
        var sb = new StringBuilder();
        if (firstName != null) {
            sb.append(firstName);
        }
        if (!sb.isEmpty()) {
            sb.append(' ');
        }
        if (middleName != null) {
            sb.append(middleName);
        }
        if (!sb.isEmpty()) {
            sb.append(' ');
        }
        if (lastName != null) {
            sb.append(lastName);
        }
        return sb.toString();
    }

    public String toFullName(EmployeeReference employeeReference) {
        return toFullName(employeeReference.firstName(), employeeReference.middleName(), employeeReference.lastName());
    }

    public String toFullName(EmployeeData employeeData) {
        return toFullName(employeeData.firstName(), employeeData.middleName(), employeeData.lastName());
    }
}
