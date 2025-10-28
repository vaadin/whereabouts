package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.humanresources.EmployeeId;

public final class EmployeeIdConverter extends AbstractValueObjectConverter<Long, EmployeeId> {

    public EmployeeIdConverter() {
        super(Long.class, EmployeeId.class, EmployeeId::new, EmployeeId::value);
    }
}
