package com.example.application.humanresources;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record EmploymentDetails(EmployeeId id, long version,
                                EmploymentDetailsData data) implements Entity<EmployeeId> {

    public EmploymentDetails withData(EmploymentDetailsData data) {
        return new EmploymentDetails(id, version, data);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (EmploymentDetails) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
