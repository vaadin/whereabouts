package com.example.application.taskmanagement.domain;

import com.example.application.taskmanagement.dto.ProjectListItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
               select p.id, p.name, count(distinct t), count(distinct a)
               from Project p
               left join Task t on t.project = p
               left join t.assignees a
               group by p.id, p.name
            """)
    List<ProjectListItem> findAllProjectListItems(Pageable pageable);

    @Query("""
                select p.id, p.name, count(distinct t), count(distinct a)
                from Project p
                left join Task t on t.project = p
                left join t.assignees a
                where lower(p.name) like concat('%', lower(:searchTerm), '%')
                group by p.id, p.name
            """)
    List<ProjectListItem> findProjectListItemsBySearchTerm(String searchTerm, Pageable pageable);

    @Query("""
                select p.id, p.name, count(distinct t), count(distinct a)
                from Project p
                left join Task t on t.project = p
                left join t.assignees a
                where p.id = :projectId
                group by p.id, p.name
            """)
    Optional<ProjectListItem> findProjectListItemById(Long projectId);
}
