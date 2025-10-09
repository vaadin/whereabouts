package com.example.application.humanresources;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record EmployeeReference(EmployeeId id, String firstName, String lastName, @Nullable String title) {
}
