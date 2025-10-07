package com.example.application.humanresources.employee;

import com.example.application.common.AggregateRoot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Employee(EmployeeId id, long version,
                       EmployeeData data) implements AggregateRoot<EmployeeId, EmployeeData> {

    public Employee withData(EmployeeData data) {
        return new Employee(id, version, data);
    }
}
