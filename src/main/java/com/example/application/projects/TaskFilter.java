package com.example.application.projects;

import com.example.application.common.SetUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

@NullMarked
public record TaskFilter(@Nullable String searchTerm, Set<TaskStatus> statuses, Set<TaskPriority> priorities) {

    public TaskFilter(@Nullable String searchTerm, Set<TaskStatus> statuses, Set<TaskPriority> priorities) {
        this.searchTerm = searchTerm;
        this.statuses = Set.copyOf(statuses);
        this.priorities = Set.copyOf(priorities);
    }

    public TaskFilter withSearchTerm(@Nullable String searchTerm) {
        return new TaskFilter(searchTerm, statuses, priorities);
    }

    public TaskFilter withStatus(TaskStatus status) {
        return new TaskFilter(searchTerm, SetUtil.add(statuses, status), priorities);
    }

    public TaskFilter withoutStatus(TaskStatus status) {
        return new TaskFilter(searchTerm, SetUtil.remove(statuses, status), priorities);
    }

    public TaskFilter withPriority(TaskPriority priority) {
        return new TaskFilter(searchTerm, statuses, SetUtil.add(priorities, priority));
    }

    public TaskFilter withoutPriority(TaskPriority priority) {
        return new TaskFilter(searchTerm, statuses, SetUtil.remove(priorities, priority));
    }

    public static TaskFilter empty() {
        return new TaskFilter(null, Collections.emptySet(), Collections.emptySet());
    }
}
