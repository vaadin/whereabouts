package com.example.whereabouts.humanresources.internal;

import com.example.whereabouts.IntegrationTest;
import com.example.whereabouts.humanresources.EmploymentDetailsData;
import com.example.whereabouts.humanresources.EmploymentStatus;
import com.example.whereabouts.humanresources.EmploymentType;
import com.example.whereabouts.humanresources.WorkArrangement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class EmploymentDetailsRepositoryTest {

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmploymentDetailsRepository repository;

    @Test
    void insert_get_and_update_include_all_properties() {
        var employeeId = employeeRepository.insert(EmployeeRepositoryTest.createEmployeeData(0));
        var managerId = employeeRepository.insert(EmployeeRepositoryTest.createEmployeeData(1));
        var locationId = locationRepository.insert(LocationRepositoryTest.createLocationData());
        var originalData = new EmploymentDetailsData(
                "Title",
                EmploymentType.FULL_TIME,
                EmploymentStatus.ACTIVE,
                WorkArrangement.HYBRID,
                locationId,
                managerId,
                LocalDate.of(2023, 5, 31),
                null
        );

        var returned = repository.insert(employeeId, originalData);
        assertThat(returned.id()).isEqualTo(employeeId);
        assertThat(returned.version()).isEqualTo(1);
        assertThat(returned.data()).isEqualTo(originalData);

        var retrieved = repository.findById(employeeId).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(employeeId);
        assertThat(retrieved.version()).isEqualTo(1);
        assertThat(retrieved.data()).isEqualTo(originalData);

        var updatedData = new EmploymentDetailsData(
                "Title2",
                EmploymentType.PART_TIME,
                EmploymentStatus.TERMINATED,
                WorkArrangement.REMOTE,
                locationId,
                managerId,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2025, 10, 8)
        );

        var updated = repository.update(retrieved.withData(updatedData));
        assertThat(updated.id()).isEqualTo(employeeId);
        assertThat(updated.version()).isEqualTo(2);
        assertThat(updated.data()).isEqualTo(updatedData);

        retrieved = repository.findById(employeeId).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(employeeId);
        assertThat(retrieved.version()).isEqualTo(2);
        assertThat(retrieved.data()).isEqualTo(updatedData);
    }
}
