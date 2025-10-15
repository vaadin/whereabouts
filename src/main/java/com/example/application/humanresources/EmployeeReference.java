package com.example.application.humanresources;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record EmployeeReference(EmployeeId id, @Nullable String firstName, @Nullable String middleName,
                                @Nullable String lastName, @Nullable Country country, @Nullable String title) {

    public static EmployeeReference of(EmployeeId id) {
        return new EmployeeReference(id, null, null, null, null, null);
    }
}
