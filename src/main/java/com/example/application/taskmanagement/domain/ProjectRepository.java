package com.example.application.taskmanagement.domain;

import com.example.application.taskmanagement.dto.ProjectListItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("select p.id, p.name, count(t) from Project p left join Task t on t.project = p group by p")
    List<ProjectListItem> findAllProjectListItems(Pageable pageable);

    @Query("select p.id, p.name, count(t) from Project p left join Task t on t.project = p where lower(p.name) like lower(:searchTerm) group by p")
    List<ProjectListItem> findProjectListItemsBySearchTerm(String searchTerm, Pageable pageable);

    @Query("select p.id, p.name, count(t) from Project p left join Task t on t.project = p where p.id = :projectId group by p")
    Optional<ProjectListItem> findProjectListItemById(Long projectId);
}
