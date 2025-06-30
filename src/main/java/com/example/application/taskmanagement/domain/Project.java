package com.example.application.taskmanagement.domain;

import com.example.application.base.domain.AbstractEntity;
import jakarta.persistence.*;
import org.jetbrains.annotations.Nullable;

@Entity
@Table(name = "project")
public class Project extends AbstractEntity<Long> {

    public static final int MAX_NAME_LENGTH = 200;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_seq")
    @SequenceGenerator(name = "project_id_seq", sequenceName = "project_id_seq")
    @Column(name = "project_id")
    private Long id;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @Column(name = "name", nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    protected Project() { // To keep JPA happy
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
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name too long");
        }
        this.name = name;
    }
}
