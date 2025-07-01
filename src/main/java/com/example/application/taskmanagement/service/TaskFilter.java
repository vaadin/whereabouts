package com.example.application.taskmanagement.service;

import com.example.application.taskmanagement.domain.Task;
import com.example.application.taskmanagement.domain.TaskPriority;
import com.example.application.taskmanagement.domain.TaskSpecifications;
import com.example.application.taskmanagement.domain.TaskStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TaskFilter {

    private @Nullable String searchTerm;
    private final Set<TaskStatus> statuses = new HashSet<>();
    private final Set<TaskPriority> priorities = new HashSet<>();

    public void setSearchTerm(@Nullable String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public void include(TaskStatus status) {
        statuses.add(status);
    }

    public void exclude(TaskStatus status) {
        statuses.remove(status);
    }

    public void include(TaskPriority priority) {
        priorities.add(priority);
    }

    public void exclude(TaskPriority priority) {
        priorities.remove(priority);
    }

    public boolean isEmpty() {
        return (searchTerm == null || searchTerm.isEmpty()) && statuses.isEmpty() && priorities.isEmpty();
    }

    Specification<Task> toSpecification() {
        var specifications = new ArrayList<Specification<Task>>();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            specifications.add(TaskSpecifications.bySearchTerm(searchTerm));
        }
        if (!statuses.isEmpty()) {
            specifications.add(TaskSpecifications.byStatus(statuses));
        }
        if (!priorities.isEmpty()) {
            specifications.add(TaskSpecifications.byPriority(priorities));
        }
        return Specification.allOf(specifications);
    }
}
