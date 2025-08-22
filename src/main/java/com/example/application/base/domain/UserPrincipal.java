package com.example.application.base.domain;

import com.example.application.AppRoles;
import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "app_user_principal")
@NullMarked
public class UserPrincipal extends AbstractEntity<Long> implements UserDetails {

    @Id
    @Column(name = "user_id")
    @Nullable
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "encoded_password")
    @Nullable
    private String encodedPassword;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "admin")
    private boolean admin;

    @Transient
    @Nullable
    private Set<GrantedAuthority> authorities;

    protected UserPrincipal() { // To keep Hibernate happy
    }

    public UserPrincipal(User user, @Nullable String encodedPassword, boolean enabled, boolean admin) {
        this.id = user.getId();
        this.user = user;
        this.encodedPassword = encodedPassword;
        this.enabled = enabled;
        this.admin = admin;
    }

    @Override
    public @Nullable Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    @Override
    public @Nullable String getPassword() {
        return encodedPassword;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (!enabled) {
            return Collections.emptySet();
        }
        if (authorities == null) {
            if (admin) {
                authorities = Set.of(createRole(AppRoles.USER), createRole(AppRoles.ADMIN));
            } else {
                authorities = Set.of(createRole(AppRoles.USER));
            }
        }
        return authorities;
    }

    private static GrantedAuthority createRole(String role) {
        return new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
    }
}
