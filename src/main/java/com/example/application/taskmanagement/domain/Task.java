package com.example.application.taskmanagement.domain;

import com.example.application.base.domain.AbstractEntity;
import com.example.application.security.domain.UserId;
import com.example.application.security.domain.jpa.UserIdAttributeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "task")
public class Task extends AbstractEntity<Long> {

    public static final int DESCRIPTION_MAX_LENGTH = 255;

    public static final String STATUS_SORT_PROPERTY = "status";
    public static final String DESCRIPTION_SORT_PROPERTY = "description";
    public static final String DUE_DATE_SORT_PROPERTY = "dueDateTime";
    public static final String PRIORITY_SORT_PROPERTY = "priority";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_id_seq")
    @SequenceGenerator(name = "task_id_seq", sequenceName = "task_id_seq")
    @Column(name = "task_id")
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "description", nullable = false, length = DESCRIPTION_MAX_LENGTH)
    @Size(max = DESCRIPTION_MAX_LENGTH)
    private String description = "";

    @Column(name = "due_date")
    @Nullable
    private LocalDate dueDate;

    @Column(name = "due_time")
    @Nullable
    private LocalTime dueTime;

    @Column(name = "time_zone", nullable = false)
    private ZoneId timeZone;

    @Column(name = "due_date_time")
    @Nullable
    private ZonedDateTime dueDateTime;

    @Enumerated
    @Column(name = "task_status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated
    @Column(name = "task_priority", nullable = false)
    private TaskPriority priority = TaskPriority.NORMAL;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_assignee", joinColumns = @JoinColumn(name = "task_id"))
    @Convert(converter = UserIdAttributeConverter.class)
    @Column(name = "assignee", nullable = false)
    private Set<UserId> assignees = new HashSet<>();

    protected Task() { // To keep Hibernate happy
    }

    public Task(Project project, ZoneId timeZone) {
        this.project = requireNonNull(project);
        this.timeZone = requireNonNull(timeZone);
    }

    public Task(Project project, ZoneId timeZone, String description, TaskStatus status, TaskPriority priority) {
        this(project, timeZone);
        setDescription(description);
        setStatus(status);
        setPriority(priority);
    }

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = requireNonNull(description);
    }

    public @Nullable LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(@Nullable LocalDate dueDate) {
        this.dueDate = dueDate;
        updateDueDateTime();
    }

    public @Nullable LocalTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(@Nullable LocalTime dueTime) {
        this.dueTime = dueTime;
        updateDueDateTime();
    }

    private void updateDueDateTime() {
        if (dueDate == null) {
            dueDateTime = null;
            return;
        }

        var time = (dueTime != null) ? dueTime : LocalTime.of(23, 59, 59); // end of day

        dueDateTime = dueDate.atTime(time).atZone(timeZone);
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public @Nullable ZonedDateTime getDueDateTimeInZone(ZoneId timeZone) {
        return dueDateTime == null ? null : dueDateTime.withZoneSameInstant(timeZone);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = requireNonNull(status);
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = requireNonNull(priority);
    }

    public Set<UserId> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<UserId> assignees) {
        this.assignees = requireNonNull(assignees);
    }
}
