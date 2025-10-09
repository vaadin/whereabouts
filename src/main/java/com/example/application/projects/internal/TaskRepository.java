package com.example.application.projects.internal;

import com.example.application.common.Repository;
import com.example.application.projects.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;

import java.util.List;

@NullMarked
public interface TaskRepository extends Repository {

    TaskId insert(TaskData data);

    Task update(Task task);

    void deleteById(TaskId id);

    List<Task> findAll(ProjectId project, Pageable pageable);

    List<Task> findByFilter(ProjectId project, TaskFilter filter, Pageable pageable);
}
