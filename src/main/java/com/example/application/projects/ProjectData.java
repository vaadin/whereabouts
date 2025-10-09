package com.example.application.projects;

import com.example.application.common.ValueObject;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record ProjectData(
        String name,
        @Nullable String description
) implements ValueObject {
    public static final String PROP_NAME = "name";
    public static final String PROP_DESCRIPTION = "description";
    public static final int NAME_MAX_LENGTH = 200;

    public ProjectData {
        if (name.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Project name is too long");
        }
    }
}
