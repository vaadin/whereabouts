package com.example.application.projects.service;

import com.example.application.TestcontainersConfiguration;
import com.example.application.base.domain.User;
import com.example.application.base.domain.UserRepository;
import com.example.application.projects.ProjectService;
import com.example.application.projects.domain.Project;
import com.example.application.projects.domain.ProjectRepository;
import com.example.application.projects.domain.Task;
import com.example.application.projects.domain.TaskRepository;
import com.example.application.security.AppRoles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
class ProjectServiceTest {

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @WithMockUser(roles = AppRoles.ADMIN)
    @Test
    void administrators_can_save_projects() {
        var project = projectService.saveProject(new Project("Test Project"));
        assertThat(projectService.hasProjects()).isTrue();
        assertThat(projectRepository.existsById(project.requireId())).isTrue();
    }

    @WithMockUser(roles = AppRoles.USER)
    @Test
    void users_cannot_save_projects() {
        assertThatThrownBy(() -> projectService.saveProject(new Project())).isInstanceOf(AccessDeniedException.class);
        assertThat(projectService.hasProjects()).isFalse();
    }

    private void saveTestProjects() {
        projectRepository.save(new Project("Test project"));
        projectRepository.save(new Project("Another test project"));
        projectRepository.save(new Project("A third project"));
    }

    @WithMockUser(roles = AppRoles.USER)
    @Test
    void returns_all_projects_for_empty_search_term() {
        saveTestProjects();
        assertThat(projectService.findProjectListItems(null, PageRequest.ofSize(10))).hasSize(3);
        assertThat(projectService.findProjectListItems("", PageRequest.ofSize(10))).hasSize(3);
    }

    @WithMockUser(roles = AppRoles.USER)
    @Test
    void returns_matching_projects_for_non_empty_search_term() {
        saveTestProjects();
        assertThat(projectService.findProjectListItems("test", PageRequest.ofSize(10))).hasSize(2);
        assertThat(projectService.findProjectListItems("third", PageRequest.ofSize(10))).hasSize(1);
        assertThat(projectService.findProjectListItems("nonexistent", PageRequest.ofSize(10))).hasSize(0);
    }

    @WithMockUser(roles = AppRoles.USER)
    @Test
    void includes_task_and_assignee_counts() {
        var project = projectRepository.save(new Project("Test project"));

        var itemWithNoTask = projectService.findProjectListItemById(project.requireId()).orElseThrow();
        assertThat(itemWithNoTask.tasks()).isZero();
        assertThat(itemWithNoTask.assignees()).isZero();

        var user1 = userRepository.save(new User("user1", "First User"));
        var user2 = userRepository.save(new User("user2", "Second User"));

        var task1 = new Task(project, ZoneId.systemDefault());
        task1.setAssignees(Set.of(user1));
        taskRepository.save(task1);

        var itemWithOneTask = projectService.findProjectListItemById(project.requireId()).orElseThrow();
        assertThat(itemWithOneTask.tasks()).isOne();
        assertThat(itemWithOneTask.assignees()).isOne();

        var task2 = new Task(project, ZoneId.systemDefault());
        task2.setAssignees(Set.of(user1, user2));
        taskRepository.save(task2);

        var itemWithTwoTasks = projectService.findProjectListItemById(project.requireId()).orElseThrow();
        assertThat(itemWithTwoTasks.tasks()).isEqualTo(2);
        assertThat(itemWithTwoTasks.assignees()).isEqualTo(2);
    }
}
