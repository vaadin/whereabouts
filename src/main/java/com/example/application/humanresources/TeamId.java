package com.example.application.humanresources;

import com.example.application.common.AbstractLongId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TeamId extends AbstractLongId {

    private TeamId(long value) {
        super(value);
    }

    public static TeamId of(long value) {
        return new TeamId(value);
    }
}
