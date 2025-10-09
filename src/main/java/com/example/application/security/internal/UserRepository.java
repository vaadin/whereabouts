package com.example.application.security.internal;

import com.example.application.security.UserId;

import java.util.Set;

public interface UserRepository {
    UserId insert(String username, String password, Set<String> roles);
}
