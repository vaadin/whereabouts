package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Entity;
import org.jspecify.annotations.NullMarked;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public record Employee(EmployeeId id, long version,
                       EmployeeData data) implements Entity<EmployeeId> {

    public Employee withData(EmployeeData data) {
        return new Employee(id, version, data);
    }
}
