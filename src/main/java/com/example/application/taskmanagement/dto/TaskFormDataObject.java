package com.example.application.taskmanagement.dto;

import com.example.application.security.domain.UserId;
import com.example.application.taskmanagement.TaskPriority;
import com.example.application.taskmanagement.TaskStatus;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

public record TaskFormDataObject(String description, @Nullable LocalDate dueDate, @Nullable LocalTime dueTime,
                                 @Nullable ZoneId timeZone, TaskStatus status, TaskPriority priority,
                                 Set<UserId> assignees) {
    public static final String PROP_DESCRIPTION = "description";
    public static final String PROP_DUE_DATE = "dueDate";
    public static final String PROP_DUE_TIME = "dueTime";
    public static final String PROP_TIME_ZONE = "timeZone";
    public static final String PROP_STATUS = "status";
    public static final String PROP_PRIORITY = "priority";
    public static final String PROP_ASSIGNEES = "assignees";
    public static final int MAX_DESCRIPTION_LENGTH = 255;
}
