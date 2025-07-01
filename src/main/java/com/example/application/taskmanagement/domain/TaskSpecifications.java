package com.example.application.taskmanagement.domain;

import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public final class TaskSpecifications {

    private TaskSpecifications() {
    }

    public static Specification<Task> byProject(Project project) {
        return (root, query, cb) -> cb.equal(root.get(Task_.project), project);
    }

    public static Specification<Task> bySearchTerm(String searchTerm) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Task_.DESCRIPTION)),
                "%" + searchTerm.toLowerCase() + "%");
    }

    public static Specification<Task> byStatus(Set<TaskStatus> statuses) {
        return (root, query, cb) -> root.get(Task_.status).in(statuses);
    }

    public static Specification<Task> byPriority(Set<TaskPriority> priorities) {
        return ((root, query, cb) -> root.get(Task_.priority).in(priorities));
    }
}
