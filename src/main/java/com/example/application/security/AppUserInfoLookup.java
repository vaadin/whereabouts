package com.example.application.security;

import com.example.application.security.domain.UserId;

import java.util.List;
import java.util.Optional;

public interface AppUserInfoLookup {

    Optional<AppUserInfo> findUserInfo(UserId userId);

    List<AppUserInfo> findUsers(String searchTerm, int limit, int offset);
}
