package com.example.application.humanresources;

import com.example.application.common.AbstractLongId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EmployeeId extends AbstractLongId {

    private EmployeeId(long value) {
        super(value);
    }

    public static EmployeeId of(long value) {
        return new EmployeeId(value);
    }
}
