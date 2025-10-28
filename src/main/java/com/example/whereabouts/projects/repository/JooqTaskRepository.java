package com.example.whereabouts.projects.repository;

import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.projects.*;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.whereabouts.jooq.Sequences.TASK_ID_SEQ;
import static com.example.whereabouts.jooq.Tables.TASK;
import static com.example.whereabouts.jooq.Tables.TASK_ASSIGNEE;

@Component
class JooqTaskRepository implements TaskRepository {

    private static final Field<Result<Record1<EmployeeId>>> ASSIGNEES = DSL.multiset(
            DSL.select(TASK_ASSIGNEE.EMPLOYEE_ID).from(TASK_ASSIGNEE).where(TASK_ASSIGNEE.TASK_ID.eq(TASK.TASK_ID))
    );
    private final DSLContext dsl;

    JooqTaskRepository(@NonNull DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public @NonNull TaskId insert(@NonNull TaskData data) {
        var id = new TaskId(dsl.nextval(TASK_ID_SEQ));
        dsl.insertInto(TASK)
                .set(TASK.TASK_ID, id)
                .set(TASK.VERSION, 1L)
                .set(TASK.PROJECT_ID, data.project())
                .set(TASK.DESCRIPTION, data.description())
                .set(TASK.DUE_DATE, data.dueDate())
                .set(TASK.DUE_TIME, data.dueTime())
                .set(TASK.TIME_ZONE, data.timeZone())
                .set(TASK.DUE_DATE_TIME, data.dueDateTime())
                .set(TASK.TASK_STATUS, data.status())
                .set(TASK.TASK_PRIORITY, data.priority())
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
                .set(TASK.PROJECT_ID, task.data().project())
                .set(TASK.DESCRIPTION, task.data().description())
                .set(TASK.DUE_DATE, task.data().dueDate())
                .set(TASK.DUE_TIME, task.data().dueTime())
                .set(TASK.TIME_ZONE, task.data().timeZone())
                .set(TASK.DUE_DATE_TIME, task.data().dueDateTime())
                .set(TASK.TASK_STATUS, task.data().status())
                .set(TASK.TASK_PRIORITY, task.data().priority())
                .where(TASK.TASK_ID.eq(task.id()))
                .and(TASK.VERSION.eq(task.version()))
                .execute();

        if (rowsUpdated == 0) {
            throw new OptimisticLockingFailureException("Task was modified by another user");
        }

        dsl.deleteFrom(TASK_ASSIGNEE)
                .where(TASK_ASSIGNEE.TASK_ID.eq(task.id()))
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
                    record.setTaskId(taskId);
                    record.setEmployeeId(assignee);
                    return record;
                }).toList();

        dsl.batchInsert(batch).execute();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteById(@NonNull TaskId id) {
        dsl.deleteFrom(TASK_ASSIGNEE)
                .where(TASK_ASSIGNEE.TASK_ID.eq(id))
                .execute();
        dsl.deleteFrom(TASK)
                .where(TASK.TASK_ID.eq(id))
                .execute();
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public @NonNull Optional<Task> findById(@NonNull TaskId id) {
        return dsl
                .select(TASK.TASK_ID,
                        TASK.VERSION,
                        TASK.PROJECT_ID,
                        TASK.DESCRIPTION,
                        TASK.DUE_DATE,
                        TASK.DUE_TIME,
                        TASK.TIME_ZONE,
                        TASK.TASK_STATUS,
                        TASK.TASK_PRIORITY,
                        ASSIGNEES
                )
                .from(TASK)
                .where(TASK.TASK_ID.eq(id))
                .fetchOptional(this::toTask);
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public @NonNull Stream<Task> findByFilter(@NonNull ProjectId project, @NonNull TaskFilter filter, int limit, int offset, @NonNull List<SortOrder<TaskSortableProperty>> sortOrders) {
        Condition condition = DSL.trueCondition();
        if (!filter.searchTerm().isBlank()) {
            condition = condition.and(TASK.DESCRIPTION.containsIgnoreCase(filter.searchTerm()));
        }
        if (!filter.statuses().isEmpty()) {
            condition = condition.and(TASK.TASK_STATUS.in(filter.statuses()));
        }
        if (!filter.priorities().isEmpty()) {
            condition = condition.and(TASK.TASK_PRIORITY.in(filter.priorities()));
        }
        return dsl
                .select(TASK.TASK_ID,
                        TASK.VERSION,
                        TASK.PROJECT_ID,
                        TASK.DESCRIPTION,
                        TASK.DUE_DATE,
                        TASK.DUE_TIME,
                        TASK.TIME_ZONE,
                        TASK.TASK_STATUS,
                        TASK.TASK_PRIORITY,
                        ASSIGNEES
                )
                .from(TASK)
                .where(TASK.PROJECT_ID.eq(project))
                .and(condition)
                .orderBy(sortOrders.stream().map(this::toOrderField).toList())
                .limit(limit)
                .offset(offset)
                .fetch(this::toTask)
                .stream();
    }

    private @NonNull Task toTask(@NonNull Record record) {
        return new Task(
                record.getValue(TASK.TASK_ID),
                record.getValue(TASK.VERSION),
                toTaskData(record));
    }

    private @NonNull TaskData toTaskData(@NonNull Record record) {
        return new TaskData(
                record.getValue(TASK.PROJECT_ID),
                record.getValue(TASK.DESCRIPTION),
                record.getValue(TASK.DUE_DATE),
                record.getValue(TASK.DUE_TIME),
                record.getValue(TASK.TIME_ZONE),
                record.getValue(TASK.TASK_STATUS),
                record.getValue(TASK.TASK_PRIORITY),
                record.getValue(ASSIGNEES).intoSet(TASK_ASSIGNEE.EMPLOYEE_ID)
        );
    }

    private @NonNull OrderField<?> toOrderField(@NonNull SortOrder<TaskSortableProperty> sortOrder) {
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
