package com.example.application.taskmanagement.domain;

import com.example.application.base.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "project")
public class Project extends AbstractEntity<Long> {

    public static final int NAME_MAX_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_seq")
    @SequenceGenerator(name = "project_id_seq", sequenceName = "project_id_seq")
    @Column(name = "project_id")
    private Long id;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    @Size(max = NAME_MAX_LENGTH)
    private String name = "";

    public Project() {
    }

    public Project(String name) {
        setName(name);
    }

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireNonNull(name);
    }
}
