package com.example.application.projects;

import com.example.application.common.EmailAddress;
import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.PersonNameFormatter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public record TaskAssignee(EmployeeId id, @Nullable String firstName, @Nullable String lastName,
                           @Nullable EmailAddress email) {

    // TODO Should this record actually be an EmployeeReference?

    public String displayName() {
        // TODO Should this method be moved to the UI?
        return PersonNameFormatter.firstLast().toFullName(firstName, lastName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (TaskAssignee) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static TaskAssignee of(EmployeeId id) {
        return new TaskAssignee(id, null, null, null);
    }
}
