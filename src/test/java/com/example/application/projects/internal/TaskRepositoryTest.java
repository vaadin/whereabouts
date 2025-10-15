package com.example.application.projects.internal;

import com.example.application.IntegrationTest;
import com.example.application.humanresources.EmployeeTestDataService;
import com.example.application.projects.TaskData;
import com.example.application.projects.TaskPriority;
import com.example.application.projects.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class TaskRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeTestDataService employeeTestDataService;
    @Autowired
    private TaskRepository repository;

    @Test
    void insert_get_and_update_include_all_properties() {
        var employee1 = employeeTestDataService.createEmployee();
        var employee2 = employeeTestDataService.createEmployee();
        var employee3 = employeeTestDataService.createEmployee();
        var project = projectRepository.insert(ProjectRepositoryTest.createProjectData());
        var originalData = new TaskData(project,
                "Description",
                LocalDate.of(2025, 10, 10),
                LocalTime.of(16, 15),
                ZoneId.of("Europe/Helsinki"),
                TaskStatus.PENDING,
                TaskPriority.URGENT,
                Set.of(employee1, employee2));
        var id = repository.insert(originalData);

        var retrieved = repository.findById(id).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(1);
        assertThat(retrieved.data()).isEqualTo(originalData);

        var updatedData = new TaskData(project,
                "Description2",
                LocalDate.of(2025, 11, 12),
                LocalTime.of(17, 16),
                ZoneId.of("Europe/Stockholm"),
                TaskStatus.PLANNED,
                TaskPriority.LOW,
                Set.of(employee3));

        var updated = repository.update(retrieved.withData(updatedData));
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.version()).isEqualTo(2);
        assertThat(updated.data()).isEqualTo(updatedData);

        retrieved = repository.findById(id).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(2);
        assertThat(retrieved.data()).isEqualTo(updatedData);
    }
}
