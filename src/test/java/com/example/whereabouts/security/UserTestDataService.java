package com.example.whereabouts.security;

import com.example.whereabouts.security.internal.UserRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTestDataService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    UserTestDataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void createTestUsers() {
        var testPassword = passwordEncoder.encode("2smart4u");
        userRepository.insert("readonly", testPassword, "Read Only", AppRoles.READ_ONlY);
        userRepository.insert("location-writer", testPassword, "Location Writer", AppRoles.LOCATION_WRITE);
        userRepository.insert("employee-writer", testPassword, "Employee Writer", AppRoles.EMPLOYEE_WRITE);
        userRepository.insert("project-writer", testPassword, "Project Writer", AppRoles.PROJECT_WRITE);
        userRepository.insert("task-writer", testPassword, "Task Writer", AppRoles.TASK_WRITE);
        userRepository.insert("admin", testPassword, "Administrator", AppRoles.ALL);
    }
}
