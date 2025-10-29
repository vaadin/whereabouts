package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.jooq.enums.EmploymentType;

public final class EmploymentTypeConverter extends AbstractEnumConverter<EmploymentType, com.example.whereabouts.humanresources.EmploymentType> {

    public EmploymentTypeConverter() {
        super(EmploymentType.class, com.example.whereabouts.humanresources.EmploymentType.class);
    }
}
