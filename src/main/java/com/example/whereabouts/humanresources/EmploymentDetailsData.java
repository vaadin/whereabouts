package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.ValueObject;
import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

@NullMarked
public record EmploymentDetailsData(
        String jobTitle,
        EmploymentType type,
        EmploymentStatus status,
        WorkArrangement workArrangement,
        LocationId location,
        @Nullable EmployeeId manager,
        LocalDate hireDate,
        @Nullable LocalDate terminationDate
) implements ValueObject {
    public static final String PROP_JOB_TITLE = "jobTitle";
    public static final String PROP_TYPE = "type";
    public static final String PROP_STATUS = "status";
    public static final String PROP_WORK_ARRANGEMENT = "workArrangement";
    public static final String PROP_LOCATION = "location";
    public static final String PROP_MANAGER = "manager";
    public static final String PROP_HIRE_DATE = "hireDate";
    public static final String PROP_TERMINATION_DATE = "terminationDate";

    public EmploymentDetailsData {
        switch (status) {
            case ACTIVE, INACTIVE -> Validate.isTrue(terminationDate == null,
                    "Termination date must be null when status is active or inactive");
            case TERMINATED -> Validate.notNull(terminationDate,
                    "Termination date must be null when status is terminated");
        }

        if (terminationDate != null) {
            Validate.isTrue(terminationDate.isAfter(hireDate), "Termination date must be after hire date");
        }
    }
}
