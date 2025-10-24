package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Entity;
import org.jspecify.annotations.NullMarked;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public record EmploymentDetails(EmployeeId id, long version,
                                EmploymentDetailsData data) implements Entity<EmployeeId> {

    public EmploymentDetails withData(EmploymentDetailsData data) {
        return new EmploymentDetails(id, version, data);
    }
}
