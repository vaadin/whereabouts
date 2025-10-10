package com.example.application.projects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
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
        return new TaskFilter(searchTerm, add(statuses, status), priorities);
    }

    public TaskFilter withoutStatus(TaskStatus status) {
        return new TaskFilter(searchTerm, remove(statuses, status), priorities);
    }

    public TaskFilter withPriority(TaskPriority priority) {
        return new TaskFilter(searchTerm, statuses, add(priorities, priority));
    }

    public TaskFilter withoutPriority(TaskPriority priority) {
        return new TaskFilter(searchTerm, statuses, remove(priorities, priority));
    }

    public static TaskFilter empty() {
        return new TaskFilter(null, Collections.emptySet(), Collections.emptySet());
    }

    private static <T> Set<T> add(Set<T> items, T itemToAdd) {
        if (items.contains(itemToAdd)) {
            return items;
        }
        var newSet = new HashSet<>(items);
        newSet.add(itemToAdd);
        return newSet;
    }

    private static <T> Set<T> remove(Set<T> items, T itemToRemove) {
        if (!items.contains(itemToRemove)) {
            return items;
        }
        var newSet = new HashSet<>(items);
        newSet.remove(itemToRemove);
        return newSet;
    }
}
