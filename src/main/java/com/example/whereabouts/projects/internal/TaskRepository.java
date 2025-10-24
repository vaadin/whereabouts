package com.example.whereabouts.projects.internal;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.projects.*;
import com.vaadin.flow.data.provider.SortOrder;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 */
@NullMarked
public interface TaskRepository extends Repository {

    Optional<Task> findById(TaskId id);

    TaskId insert(TaskData data);

    Task update(Task task);

    void deleteById(TaskId id);

    Stream<Task> findByFilter(ProjectId project, TaskFilter filter, int limit, int offset, List<SortOrder<TaskSortableProperty>> sortOrders);
}
