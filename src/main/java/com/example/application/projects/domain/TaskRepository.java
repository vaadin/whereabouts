package com.example.application.projects.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Deprecated(forRemoval = true)
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    int countAllByProject(Project project);

    Slice<Task> findAllByProject(Project project, Pageable pageable);

    Slice<Task> findAllByProjectAndDescriptionContainingIgnoreCase(Project project, String description,
                                                                   Pageable pageable);
}
