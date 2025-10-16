package com.example.whereabouts.security;

import com.example.whereabouts.common.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

@NullMarked
public record User(UserId id, long version, String username, @Nullable String password, String displayName,
                   boolean enabled, Collection<String> roles) implements Entity<UserId> {
}
