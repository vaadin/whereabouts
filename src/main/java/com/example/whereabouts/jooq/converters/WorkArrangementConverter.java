package com.example.whereabouts.jooq.converters;

import com.example.whereabouts.jooq.enums.WorkArrangement;

public final class WorkArrangementConverter extends AbstractEnumConverter<WorkArrangement, com.example.whereabouts.humanresources.WorkArrangement> {

    public WorkArrangementConverter() {
        super(WorkArrangement.class, com.example.whereabouts.humanresources.WorkArrangement.class);
    }
}
