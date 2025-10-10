package com.example.application.projects.internal.jooq;

import com.example.application.common.EmailAddress;
import com.example.application.projects.TaskPriority;
import com.example.application.projects.TaskStatus;
import org.jooq.Converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

final class JooqConverters {

    private JooqConverters() {
    }

    public static Converter<com.example.application.jooq.enums.TaskPriority, TaskPriority> taskPriorityConverter = Converter.ofNullable(
            com.example.application.jooq.enums.TaskPriority.class,
            TaskPriority.class,
            dbType -> TaskPriority.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.TaskPriority.valueOf(domainType.name())
    );

    public static Converter<com.example.application.jooq.enums.TaskStatus, TaskStatus> taskStatusConverter = Converter.ofNullable(
            com.example.application.jooq.enums.TaskStatus.class,
            TaskStatus.class,
            dbType -> TaskStatus.valueOf(dbType.name()),
            domainType -> com.example.application.jooq.enums.TaskStatus.valueOf(domainType.name())
    );

    public static final Converter<String, ZoneId> zoneIdConverter = Converter.ofNullable(
            String.class, ZoneId.class, ZoneId::of, ZoneId::getId
    );

    public static final Converter<String, EmailAddress> emailConverter = Converter.ofNullable(
            String.class, EmailAddress.class, EmailAddress::of, EmailAddress::toString
    );

    public static final Converter<OffsetDateTime, ZonedDateTime> zonedDateTimeConverter = Converter.ofNullable(
            OffsetDateTime.class, ZonedDateTime.class, OffsetDateTime::toZonedDateTime, ZonedDateTime::toOffsetDateTime
    );
}
