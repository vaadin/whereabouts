package com.example.application.projects.internal;

import com.example.application.projects.TaskAssignee;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

@NullMarked
public interface TaskQuery {

    Stream<TaskAssignee> findAssigneesBySearchTerm(@Nullable String searchTerm, int limit, int offset);
}
