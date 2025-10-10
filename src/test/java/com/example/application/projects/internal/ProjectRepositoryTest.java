package com.example.application.projects.internal;

import com.example.application.IntegrationTest;
import com.example.application.projects.ProjectData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ProjectRepositoryTest {

    @Autowired
    ProjectRepository repository;

    static ProjectData createProjectData() {
        return new ProjectData("Name", "Description");
    }

    @Test
    void insert_and_get_include_all_properties() {
        var originalData = createProjectData();
        var id = repository.insert(originalData);

        var retrieved = repository.findById(id).orElseThrow();
        assertThat(retrieved.id()).isEqualTo(id);
        assertThat(retrieved.version()).isEqualTo(1);
        assertThat(retrieved.data()).isEqualTo(originalData);
    }
}
