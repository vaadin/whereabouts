package com.example.application;

import com.example.application.security.TestUserService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration-test")
class TestDataOrchestrator {
    private final TestUserService testUserService;

    TestDataOrchestrator(TestUserService testUserService) {
        this.testUserService = testUserService;
    }

    @PostConstruct
    void generateTestData() {
        testUserService.createTestUsers();
    }
}
