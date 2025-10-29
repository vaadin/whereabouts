package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.jooq.enums.EmploymentStatus;

public final class EmploymentStatusConverter extends AbstractEnumConverter<EmploymentStatus, com.example.whereabouts.humanresources.EmploymentStatus> {

    public EmploymentStatusConverter() {
        super(EmploymentStatus.class, com.example.whereabouts.humanresources.EmploymentStatus.class);
    }
}
