package com.example.application.projects.domain;

import com.example.application.base.domain.AbstractEntity;
import com.example.application.base.domain.User;
import com.example.application.projects.TaskPriority;
import com.example.application.projects.TaskStatus;
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
@Deprecated(forRemoval = true)
public class Task extends AbstractEntity<Long> {

    public static final int DESCRIPTION_MAX_LENGTH = 500;

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
    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "description")
    @Size(max = DESCRIPTION_MAX_LENGTH)
    private String description = "";

    @Column(name = "due_date")
    @Nullable
    private LocalDate dueDate;

    @Column(name = "due_time")
    @Nullable
    private LocalTime dueTime;

    @Column(name = "time_zone")
    private ZoneId timeZone;

    @Column(name = "due_date_time")
    @Nullable
    private ZonedDateTime dueDateTime;

    @Enumerated
    @Column(name = "task_status")
    private TaskStatus status = TaskStatus.PENDING;

    @Enumerated
    @Column(name = "task_priority")
    private TaskPriority priority = TaskPriority.NORMAL;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "task_assignee", joinColumns = @JoinColumn(name = "task_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> assignees = new HashSet<>();

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

    public Set<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<User> assignees) {
        this.assignees = requireNonNull(assignees);
    }
}
