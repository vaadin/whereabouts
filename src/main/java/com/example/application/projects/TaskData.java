package com.example.application.projects;

import com.example.application.common.ValueObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;

@NullMarked
public record TaskData(
        ProjectId project,
        String description,
        @Nullable LocalDate dueDate,
        @Nullable LocalTime dueTime,
        ZoneId timeZone,
        TaskStatus status,
        TaskPriority priority,
        Collection<TaskAssignee> assignees
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

    public @Nullable ZonedDateTime dueDateTimeInZone(ZoneId timeZone) {
        var dueDateTime = dueDateTime();
        return dueDateTime == null ? null : dueDateTime.withZoneSameInstant(timeZone);
    }

    public static TaskData createDefault(ProjectId project, ZoneId timeZone) {
        return new TaskData(project, "", null, null, timeZone, TaskStatus.PENDING,
                TaskPriority.NORMAL, Collections.emptySet());
    }
}
