package com.example.application.projects.internal.jooq;

import com.example.application.humanresources.EmployeeId;
import com.example.application.projects.*;
import com.example.application.projects.internal.TaskRepository;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.application.jooq.Sequences.TASK_ID_SEQ;
import static com.example.application.jooq.Tables.TASK;
import static com.example.application.jooq.Tables.TASK_ASSIGNEE;
import static com.example.application.projects.internal.jooq.JooqConverters.*;

@Component
class JooqTaskRepository implements TaskRepository {

    private static final Field<EmployeeId> ASSIGNEE_EMPLOYEE_ID = TASK_ASSIGNEE.EMPLOYEE_ID.convert(employeeIdConverter);
    private static final Field<TaskId> ASSIGNEE_TASK_ID = TASK_ASSIGNEE.TASK_ID.convert(taskIdConverter);
    private static final Field<TaskId> TASK_ID = TASK.TASK_ID.convert(taskIdConverter);
    private static final Field<ProjectId> PROJECT_ID = TASK.PROJECT_ID.convert(projectIdConverter);
    private static final Field<Result<Record1<EmployeeId>>> ASSIGNEES = DSL.multiset(
            DSL.select(ASSIGNEE_EMPLOYEE_ID).from(TASK_ASSIGNEE).where(TASK_ASSIGNEE.TASK_ID.eq(TASK.TASK_ID))
    );
    private static final Field<ZoneId> TIME_ZONE = TASK.TIME_ZONE.convert(zoneIdConverter);
    private static final Field<TaskStatus> TASK_STATUS = TASK.TASK_STATUS.convert(taskStatusConverter);
    private static final Field<TaskPriority> TASK_PRIORITY = TASK.TASK_PRIORITY.convert(taskPriorityConverter);
    private static final Field<ZonedDateTime> DUE_DATE_TIME = TASK.DUE_DATE_TIME.convert(zonedDateTimeConverter);

    private final DSLContext dsl;

    JooqTaskRepository(@NonNull DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public @NonNull TaskId insert(@NonNull TaskData data) {
        var id = TaskId.of(dsl.nextval(TASK_ID_SEQ));
        dsl.insertInto(TASK)
                .set(TASK_ID, id)
                .set(TASK.VERSION, 1L)
                .set(PROJECT_ID, data.project())
                .set(TASK.DESCRIPTION, data.description())
                .set(TASK.DUE_DATE, data.dueDate())
                .set(TASK.DUE_TIME, data.dueTime())
                .set(TIME_ZONE, data.timeZone())
                .set(DUE_DATE_TIME, data.dueDateTime())
                .set(TASK_STATUS, data.status())
                .set(TASK_PRIORITY, data.priority())
                .execute();
        insertAssignees(id, data.assignees());
        return id;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public @NonNull Task update(@NonNull Task task) {
        var newVersion = task.version() + 1;
        var rowsUpdated = dsl.update(TASK)
                .set(TASK.VERSION, newVersion)
                .set(PROJECT_ID, task.data().project())
                .set(TASK.DESCRIPTION, task.data().description())
                .set(TASK.DUE_DATE, task.data().dueDate())
                .set(TASK.DUE_TIME, task.data().dueTime())
                .set(TIME_ZONE, task.data().timeZone())
                .set(DUE_DATE_TIME, task.data().dueDateTime())
                .set(TASK_STATUS, task.data().status())
                .set(TASK_PRIORITY, task.data().priority())
                .where(TASK_ID.eq(task.id()))
                .and(TASK.VERSION.eq(task.version()))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Task was modified by another user");
        }

        dsl.deleteFrom(TASK_ASSIGNEE)
                .where(ASSIGNEE_TASK_ID.eq(task.id()))
                .execute();

        insertAssignees(task.id(), task.data().assignees());

        return new Task(task.id(), newVersion, task.data());
    }

    private void insertAssignees(@NonNull TaskId taskId, @NonNull Collection<EmployeeId> assignees) {
        if (assignees.isEmpty()) {
            return;
        }

        var batch = assignees.stream()
                .map(assignee -> {
                    var record = dsl.newRecord(TASK_ASSIGNEE);
                    record.setTaskId(taskIdConverter.to(taskId));
                    record.setEmployeeId(employeeIdConverter.to(assignee));
                    return record;
                }).toList();

        dsl.batchInsert(batch).execute();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteById(@NonNull TaskId id) {
        dsl.deleteFrom(TASK_ASSIGNEE)
                .where(ASSIGNEE_TASK_ID.eq(id))
                .execute();
        dsl.deleteFrom(TASK)
                .where(TASK_ID.eq(id))
                .execute();
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public @NonNull Optional<Task> findById(@NonNull TaskId id) {
        return dsl
                .select(TASK_ID,
                        TASK.VERSION,
                        PROJECT_ID,
                        TASK.DESCRIPTION,
                        TASK.DUE_DATE,
                        TASK.DUE_TIME,
                        TIME_ZONE,
                        TASK_STATUS,
                        TASK_PRIORITY,
                        ASSIGNEES
                )
                .from(TASK)
                .where(TASK_ID.eq(id))
                .fetchOptional(this::toTask);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public @NonNull Stream<Task> findByFilter(@NonNull ProjectId project, @NonNull TaskFilter filter, int limit, int offset, @NonNull List<SortOrder<TaskSortableProperty>> sortOrders) {
        Condition condition = DSL.trueCondition();
        if (filter.searchTerm() != null && !filter.searchTerm().isBlank()) {
            condition = condition.and(TASK.DESCRIPTION.containsIgnoreCase(filter.searchTerm()));
        }
        if (!filter.statuses().isEmpty()) {
            condition = condition.and(TASK_STATUS.in(filter.statuses()));
        }
        if (!filter.priorities().isEmpty()) {
            condition = condition.and(TASK_PRIORITY.in(filter.priorities()));
        }
        return dsl
                .select(TASK_ID,
                        TASK.VERSION,
                        PROJECT_ID,
                        TASK.DESCRIPTION,
                        TASK.DUE_DATE,
                        TASK.DUE_TIME,
                        TIME_ZONE,
                        TASK_STATUS,
                        TASK_PRIORITY,
                        ASSIGNEES
                )
                .from(TASK)
                .where(PROJECT_ID.eq(project))
                .and(condition)
                .orderBy(sortOrders.stream().map(this::toOrderField).toList())
                .limit(limit)
                .offset(offset)
                .fetch(this::toTask)
                .stream();
    }

    private @NonNull Task toTask(@NonNull Record record) {
        return new Task(
                record.getValue(TASK_ID),
                record.getValue(TASK.VERSION),
                toTaskData(record));
    }

    private @NonNull TaskData toTaskData(@NonNull Record record) {
        return new TaskData(
                record.getValue(PROJECT_ID),
                record.getValue(TASK.DESCRIPTION),
                record.getValue(TASK.DUE_DATE),
                record.getValue(TASK.DUE_TIME),
                record.getValue(TIME_ZONE),
                record.getValue(TASK_STATUS),
                record.getValue(TASK_PRIORITY),
                record.getValue(ASSIGNEES).intoSet(ASSIGNEE_EMPLOYEE_ID)
        );
    }

    private @NonNull OrderField<?> toOrderField(@NonNull SortOrder<TaskSortableProperty> sortOrder) {
        return switch (sortOrder.getSorted()) {
            case STATUS -> sortOrder.getDirection() == SortDirection.ASCENDING ? TASK_STATUS.asc() : TASK_STATUS.desc();
            case DESCRIPTION ->
                    sortOrder.getDirection() == SortDirection.ASCENDING ? TASK.DESCRIPTION.asc() : TASK.DESCRIPTION.desc();
            case DUE_DATE ->
                    sortOrder.getDirection() == SortDirection.ASCENDING ? DUE_DATE_TIME.asc() : DUE_DATE_TIME.desc();
            case PRIORITY ->
                    sortOrder.getDirection() == SortDirection.ASCENDING ? TASK_PRIORITY.asc() : TASK_PRIORITY.desc();
        };
    }
}
