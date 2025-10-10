package com.example.application.projects.internal.jooq;

import com.example.application.common.EmailAddress;
import com.example.application.humanresources.EmployeeId;
import com.example.application.projects.*;
import com.example.application.projects.internal.TaskRepository;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.application.jooq.Sequences.TASK_ID_SEQ;
import static com.example.application.jooq.Tables.*;
import static com.example.application.projects.internal.jooq.JooqConverters.*;

@Component
@NullMarked
class JooqTaskRepository implements TaskRepository {

    private static final Field<Result<Record4<EmployeeId, String, String, EmailAddress>>> ASSIGNEES = DSL.multiset(
            DSL.select(EMPLOYEE.EMPLOYEE_ID.convertFrom(EmployeeId::of).as("converted_id"),
                            EMPLOYEE.FIRST_NAME,
                            EMPLOYEE.LAST_NAME,
                            EMPLOYEE.WORK_EMAIL.convert(emailConverter)
                    )
                    .from(TASK_ASSIGNEE)
                    .join(EMPLOYEE).on(EMPLOYEE.EMPLOYEE_ID.eq(TASK_ASSIGNEE.EMPLOYEE_ID))
                    .where(TASK_ASSIGNEE.TASK_ID.eq(TASK.TASK_ID))
    );
    private static final Field<ZoneId> TIME_ZONE = TASK.TIME_ZONE.convert(zoneIdConverter);
    private static final Field<TaskStatus> TASK_STATUS = TASK.TASK_STATUS.convert(taskStatusConverter);
    private static final Field<TaskPriority> TASK_PRIORITY = TASK.TASK_PRIORITY.convert(taskPriorityConverter);

    private final DSLContext dsl;

    JooqTaskRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public TaskId insert(TaskData data) {
        var id = TaskId.of(dsl.nextval(TASK_ID_SEQ));
        dsl.insertInto(TASK)
                .set(TASK.TASK_ID, id.toLong())
                .set(TASK.VERSION, 1L)
                .set(TASK.PROJECT_ID, data.project().toLong())
                .set(TASK.DESCRIPTION, data.description())
                .set(TASK.DUE_DATE, data.dueDate())
                .set(TASK.DUE_TIME, data.dueTime())
                .set(TASK.TIME_ZONE, zoneIdConverter.to(data.timeZone()))
                .set(TASK.DUE_DATE_TIME, zonedDateTimeConverter.to(data.dueDateTime()))
                .set(TASK.TASK_STATUS, taskStatusConverter.to(data.status()))
                .set(TASK.TASK_PRIORITY, taskPriorityConverter.to(data.priority()))
                .execute();
        insertAssignees(id, data.assignees());
        return id;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Task update(Task task) {
        var newVersion = task.version() + 1;
        var rowsUpdated = dsl.update(TASK)
                .set(TASK.VERSION, newVersion)
                .set(TASK.PROJECT_ID, task.data().project().toLong())
                .set(TASK.DESCRIPTION, task.data().description())
                .set(TASK.DUE_DATE, task.data().dueDate())
                .set(TASK.DUE_TIME, task.data().dueTime())
                .set(TASK.TIME_ZONE, zoneIdConverter.to(task.data().timeZone()))
                .set(TASK.DUE_DATE_TIME, zonedDateTimeConverter.to(task.data().dueDateTime()))
                .set(TASK.TASK_STATUS, taskStatusConverter.to(task.data().status()))
                .set(TASK.TASK_PRIORITY, taskPriorityConverter.to(task.data().priority()))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Task was modified by another user");
        }

        dsl.deleteFrom(TASK_ASSIGNEE)
                .where(TASK_ASSIGNEE.TASK_ID.eq(task.id().toLong()))
                .execute();

        insertAssignees(task.id(), task.data().assignees());

        return new Task(task.id(), newVersion, task.data());
    }

    private void insertAssignees(TaskId taskId, Collection<TaskAssignee> assignees) {
        if (assignees.isEmpty()) {
            return;
        }

        var batch = assignees.stream()
                .map(assignee -> {
                    var record = dsl.newRecord(TASK_ASSIGNEE);
                    record.setTaskId(taskId.toLong());
                    record.setEmployeeId(assignee.id().toLong());
                    return record;
                }).toList();

        dsl.batchInsert(batch).execute();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteById(TaskId id) {
        dsl.deleteFrom(TASK_ASSIGNEE)
                .where(TASK_ASSIGNEE.TASK_ID.eq(id.toLong()))
                .execute();
        dsl.deleteFrom(TASK)
                .where(TASK.TASK_ID.eq(id.toLong()))
                .execute();
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public Optional<Task> findById(TaskId id) {
        return dsl
                .select(TASK.TASK_ID,
                        TASK.VERSION,
                        TASK.PROJECT_ID,
                        TASK.DESCRIPTION,
                        TASK.DUE_DATE,
                        TASK.DUE_TIME,
                        TIME_ZONE,
                        TASK_STATUS,
                        TASK_PRIORITY,
                        ASSIGNEES
                )
                .from(TASK)
                .where(TASK.TASK_ID.eq(id.toLong()))
                .fetchOptional(this::toTask);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public Stream<Task> findByFilter(ProjectId project, TaskFilter filter, int limit, int offset, List<SortOrder<TaskSortableProperty>> sortOrders) {
        Condition condition = DSL.trueCondition();
        if (filter.searchTerm() != null && !filter.searchTerm().isBlank()) {
            condition = condition.and(TASK.DESCRIPTION.containsIgnoreCase(filter.searchTerm()));
        }
        if (!filter.statuses().isEmpty()) {
            condition = condition.and(TASK.TASK_STATUS.in(filter.statuses().stream().map(taskStatusConverter::to).collect(Collectors.toSet())));
        }
        if (!filter.priorities().isEmpty()) {
            condition = condition.and(TASK.TASK_PRIORITY.in(filter.priorities().stream().map(taskPriorityConverter::to).collect(Collectors.toSet())));
        }
        return dsl
                .select(TASK.TASK_ID,
                        TASK.VERSION,
                        TASK.PROJECT_ID,
                        TASK.DESCRIPTION,
                        TASK.DUE_DATE,
                        TASK.DUE_TIME,
                        TIME_ZONE,
                        TASK_STATUS,
                        TASK_PRIORITY,
                        ASSIGNEES
                )
                .from(TASK)
                .where(TASK.PROJECT_ID.eq(project.toLong()).and(condition))
                .orderBy(sortOrders.stream().map(this::toOrderField).toList())
                .limit(limit)
                .offset(offset)
                .fetch(this::toTask)
                .stream();
    }

    private Task toTask(Record record) {
        return new Task(
                TaskId.of(record.getValue(TASK.TASK_ID)),
                record.getValue(TASK.VERSION),
                toTaskData(record));
    }

    private TaskData toTaskData(Record record) {
        return new TaskData(
                ProjectId.of(record.getValue(TASK.PROJECT_ID)),
                record.getValue(TASK.DESCRIPTION),
                record.getValue(TASK.DUE_DATE),
                record.getValue(TASK.DUE_TIME),
                record.getValue(TIME_ZONE),
                record.getValue(TASK_STATUS),
                record.getValue(TASK_PRIORITY),
                record.getValue(ASSIGNEES).map(Records.mapping(TaskAssignee::new))
        );
    }

    private OrderField<?> toOrderField(SortOrder<TaskSortableProperty> sortOrder) {
        return switch (sortOrder.getSorted()) {
            case STATUS ->
                    sortOrder.getDirection() == SortDirection.ASCENDING ? TASK.TASK_STATUS.asc() : TASK.TASK_STATUS.desc();
            case DESCRIPTION ->
                    sortOrder.getDirection() == SortDirection.ASCENDING ? TASK.DESCRIPTION.asc() : TASK.DESCRIPTION.desc();
            case DUE_DATE ->
                    sortOrder.getDirection() == SortDirection.ASCENDING ? TASK.DUE_DATE_TIME.asc() : TASK.DUE_DATE_TIME.desc();
            case PRIORITY ->
                    sortOrder.getDirection() == SortDirection.ASCENDING ? TASK.TASK_PRIORITY.asc() : TASK.TASK_PRIORITY.desc();
        };
    }
}
