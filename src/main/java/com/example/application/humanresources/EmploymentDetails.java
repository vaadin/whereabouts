package com.example.application.humanresources;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record EmploymentDetails(EmployeeId id, long version,
                                EmploymentDetailsData data) implements Entity<EmployeeId> {

    public EmploymentDetails withData(EmploymentDetailsData data) {
        return new EmploymentDetails(id, version, data);
    }
}
