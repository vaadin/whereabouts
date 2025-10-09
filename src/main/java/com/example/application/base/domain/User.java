package com.example.application.base.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "app_user")
@NullMarked
@Deprecated(forRemoval = true)
public class User extends AbstractEntity<Long> {

    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int DISPLAY_NAME_MAX_LENGTH = 90;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "app_user_id_seq")
    @Column(name = "user_id")
    @Nullable
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "username")
    @Size(max = USERNAME_MAX_LENGTH)
    private String username = "";

    @Column(name = "display_name")
    @Size(max = DISPLAY_NAME_MAX_LENGTH)
    private String displayName = "";

    protected User() { // To keep Hibernate happy
    }

    public User(String username, String displayName) {
        setUsername(username);
        setDisplayName(displayName);
    }

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = requireNonNull(username);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = requireNonNull(displayName);
    }
}
