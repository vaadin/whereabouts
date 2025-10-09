package com.example.application.humanresources;

import com.example.application.common.ValueObject;
import org.jspecify.annotations.Nullable;

public record TeamData(
        String name,
        @Nullable String summary,
        @Nullable EmployeeId manager
) implements ValueObject {
}
