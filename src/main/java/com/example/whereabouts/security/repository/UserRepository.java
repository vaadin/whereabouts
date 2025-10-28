package com.example.whereabouts.security.repository;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.security.User;
import com.example.whereabouts.security.UserId;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@NullMarked
public interface UserRepository extends Repository {

    UserId insert(String username, @Nullable String password, String displayName, Set<String> roles);

    Optional<User> findByUsername(String username);
}
