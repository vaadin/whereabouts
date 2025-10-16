package com.example.whereabouts.projects;

import com.example.whereabouts.common.AbstractLongId;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ProjectId extends AbstractLongId {

    private ProjectId(long value) {
        super(value);
    }

    @JsonCreator
    public static ProjectId of(long value) {
        return new ProjectId(value);
    }
}
