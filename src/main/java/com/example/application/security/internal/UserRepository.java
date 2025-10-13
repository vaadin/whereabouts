package com.example.application.security.internal;

import com.example.application.common.Repository;
import com.example.application.security.User;
import com.example.application.security.UserId;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@NullMarked
public interface UserRepository extends Repository {

    UserId insert(String username, @Nullable String password, String displayName, Set<String> roles);

    Optional<User> findByUsername(String username);
}
