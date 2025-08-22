package com.example.application.base.service;

import com.example.application.base.domain.User;
import com.example.application.base.domain.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@PreAuthorize("isAuthenticated()")
@NullMarked
public class AppUserLookupService {

    private final UserRepository userRepository;

    public AppUserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findUsers(Pageable pageable, @Nullable String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return Collections.emptyList();
        } else {
            return userRepository.findBySearchTerm("%" + searchTerm + "%", pageable).getContent();
        }
    }
}
