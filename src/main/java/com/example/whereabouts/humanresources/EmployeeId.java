package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.AbstractLongId;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NullMarked;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public final class EmployeeId extends AbstractLongId {

    private EmployeeId(long value) {
        super(value);
    }

    @JsonCreator
    public static EmployeeId of(long value) {
        return new EmployeeId(value);
    }
}
