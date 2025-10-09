package com.example.application.humanresources;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Employee(EmployeeId id, long version,
                       EmployeeData data) implements Entity<EmployeeId> {

    public Employee withData(EmployeeData data) {
        return new Employee(id, version, data);
    }
}
