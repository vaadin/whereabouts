package com.example.application.humanresources;

import com.example.application.common.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record Team(TeamId id, long version, TeamData data) implements Entity<TeamId> {

    public Team withData(TeamData data) {
        return new Team(id, version, data);
    }
}
