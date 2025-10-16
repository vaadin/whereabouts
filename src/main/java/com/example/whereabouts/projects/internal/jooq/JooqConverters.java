package com.example.whereabouts.projects.internal.jooq;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.projects.ProjectId;
import com.example.whereabouts.projects.TaskId;
import com.example.whereabouts.projects.TaskPriority;
import com.example.whereabouts.projects.TaskStatus;
import org.jooq.Converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

final class JooqConverters {

    private JooqConverters() {
    }

    public static Converter<com.example.whereabouts.jooq.enums.TaskPriority, TaskPriority> taskPriorityConverter = Converter.ofNullable(
            com.example.whereabouts.jooq.enums.TaskPriority.class,
            TaskPriority.class,
            dbType -> TaskPriority.valueOf(dbType.name()),
            domainType -> com.example.whereabouts.jooq.enums.TaskPriority.valueOf(domainType.name())
    );

    public static Converter<com.example.whereabouts.jooq.enums.TaskStatus, TaskStatus> taskStatusConverter = Converter.ofNullable(
            com.example.whereabouts.jooq.enums.TaskStatus.class,
            TaskStatus.class,
            dbType -> TaskStatus.valueOf(dbType.name()),
            domainType -> com.example.whereabouts.jooq.enums.TaskStatus.valueOf(domainType.name())
    );

    public static final Converter<String, ZoneId> zoneIdConverter = Converter.ofNullable(
            String.class, ZoneId.class, ZoneId::of, ZoneId::getId
    );

    public static final Converter<String, Country> countryConverter = Converter.ofNullable(
            String.class, Country.class, Country::ofIsoCode, Country::toString
    );

    public static final Converter<OffsetDateTime, ZonedDateTime> zonedDateTimeConverter = Converter.ofNullable(
            OffsetDateTime.class, ZonedDateTime.class, OffsetDateTime::toZonedDateTime, ZonedDateTime::toOffsetDateTime
    );

    public static final Converter<Long, TaskId> taskIdConverter = Converter.ofNullable(
            Long.class, TaskId.class, TaskId::of, TaskId::toLong
    );

    public static final Converter<Long, EmployeeId> employeeIdConverter = Converter.ofNullable(
            Long.class, EmployeeId.class, EmployeeId::of, EmployeeId::toLong
    );

    public static final Converter<Long, ProjectId> projectIdConverter = Converter.ofNullable(
            Long.class, ProjectId.class, ProjectId::of, ProjectId::toLong
    );
}
