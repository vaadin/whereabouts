package com.example.whereabouts.projects.internal.jooq;

import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.jooq.converters.*;
import com.example.whereabouts.projects.ProjectId;
import com.example.whereabouts.projects.TaskId;
import com.example.whereabouts.projects.TaskPriority;
import com.example.whereabouts.projects.TaskStatus;
import org.jooq.Converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Deprecated
final class JooqConverters {

    private JooqConverters() {
    }

    @Deprecated
    public static Converter<com.example.whereabouts.jooq.enums.TaskPriority, TaskPriority> taskPriorityConverter = new TaskPriorityConverter();

    @Deprecated
    public static Converter<com.example.whereabouts.jooq.enums.TaskStatus, TaskStatus> taskStatusConverter = new TaskStatusConverter();

    @Deprecated
    public static final Converter<String, ZoneId> zoneIdConverter = new ZoneIdConverter();

    @Deprecated
    public static final Converter<OffsetDateTime, ZonedDateTime> zonedDateTimeConverter = new ZonedDateTimeConverter();

    @Deprecated
    public static final Converter<Long, TaskId> taskIdConverter = new TaskIdConverter();

    @Deprecated
    public static final Converter<Long, EmployeeId> employeeIdConverter = new EmployeeIdConverter();

    @Deprecated
    public static final Converter<Long, ProjectId> projectIdConverter = new ProjectIdConverter();
}
