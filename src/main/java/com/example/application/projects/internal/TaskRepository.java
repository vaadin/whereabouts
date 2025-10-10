package com.example.application.projects.internal;

import com.example.application.common.Repository;
import com.example.application.projects.*;
import com.vaadin.flow.data.provider.SortOrder;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@NullMarked
public interface TaskRepository extends Repository {

    Optional<Task> findById(TaskId id);

    TaskId insert(TaskData data);

    Task update(Task task);

    void deleteById(TaskId id);

    Stream<Task> findByFilter(ProjectId project, TaskFilter filter, int limit, int offset, List<SortOrder<TaskSortableProperty>> sortOrders);
}
