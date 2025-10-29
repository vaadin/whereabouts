package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.projects.ProjectId;

public final class ProjectIdConverter extends AbstractValueObjectConverter<Long, ProjectId> {

    public ProjectIdConverter() {
        super(Long.class, ProjectId.class, ProjectId::new, ProjectId::value);
    }
}
