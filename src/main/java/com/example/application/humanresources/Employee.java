package com.example.application.humanresources;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record Employee(EmployeeId id, long version,
                       EmployeeData data) implements Entity<EmployeeId> {

    public Employee withData(EmployeeData data) {
        return new Employee(id, version, data);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (Employee) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
