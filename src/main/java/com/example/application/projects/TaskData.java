package com.example.application.projects;

import com.example.application.common.ValueObject;
import com.example.application.humanresources.EmployeeId;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

@NullMarked
public record TaskData(
        ProjectId project,
        String description,
        @Nullable LocalDate dueDate,
        @Nullable LocalTime dueTime,
        ZoneId timeZone,
        TaskStatus status,
        TaskPriority priority,
        Collection<EmployeeId> assignees
) implements ValueObject {

    public static final String PROP_PROJECT = "project";
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_DUE_DATE = "dueDate";
    public static final String PROP_DUE_TIME = "dueTime";
    public static final String PROP_TIMEZONE = "timeZone";
    public static final String PROP_STATUS = "status";
    public static final String PROP_PRIORITY = "priority";
    public static final String PROP_ASSIGNEES = "assignees";
    public static final int DESCRIPTION_MAX_LENGTH = 500;

    public @Nullable ZonedDateTime dueDateTime() {
        if (dueDate == null) {
            return null;
        }
        var time = (dueTime != null) ? dueTime : LocalTime.of(23, 59, 59); // end of day
        return dueDate.atTime(time).atZone(timeZone);
    }
}
