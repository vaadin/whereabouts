package com.example.application.base.domain;

import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "app_user_principal")
@NullMarked
public class UserPrincipal extends AbstractEntity<Long> implements UserDetails {

    @Id
    @Column(name = "user_id")
    @Nullable
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "encoded_password")
    @Nullable
    private String encodedPassword;

    @Column(name = "enabled")
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "app_user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name")
    private Set<String> roles;

    protected UserPrincipal() { // To keep Hibernate happy
    }

    public UserPrincipal(User user, @Nullable String encodedPassword, boolean enabled, Set<String> roles) {
        this.id = user.getId();
        this.user = user;
        this.encodedPassword = encodedPassword;
        this.enabled = enabled;
        this.roles = Set.copyOf(roles);
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase())).toList();
    }
}
