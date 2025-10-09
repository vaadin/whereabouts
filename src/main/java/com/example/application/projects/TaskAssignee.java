package com.example.application.projects;

import com.example.application.humanresources.EmployeeId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record TaskAssignee(EmployeeId id, String displayName) {
}
