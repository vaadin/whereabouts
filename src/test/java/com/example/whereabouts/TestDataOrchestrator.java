package com.example.whereabouts;

import com.example.whereabouts.humanresources.EmployeeTestDataService;
import com.example.whereabouts.humanresources.LocationTestDataService;
import com.example.whereabouts.security.UserTestDataService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration-test")
class TestDataOrchestrator {
    private final UserTestDataService userTestDataService;
    private final LocationTestDataService locationTestDataService;
    private final EmployeeTestDataService employeeTestDataService;

    TestDataOrchestrator(UserTestDataService userTestDataService, LocationTestDataService locationTestDataService, EmployeeTestDataService employeeTestDataService) {
        this.userTestDataService = userTestDataService;
        this.locationTestDataService = locationTestDataService;
        this.employeeTestDataService = employeeTestDataService;
    }

    @PostConstruct
    void generateTestData() {
        userTestDataService.createTestUsers();
        locationTestDataService.createTestLocations();
        employeeTestDataService.createTestEmployees();
    }
}
