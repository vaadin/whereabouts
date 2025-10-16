package com.example.whereabouts.security;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public final class UserPrincipal implements UserDetails, Principal {

    private final User user;
    private final Set<GrantedAuthority> grantedAuthorities;

    public UserPrincipal(User user) {
        this.user = user;
        grantedAuthorities = user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.password();
    }

    @Override
    public String getUsername() {
        return user.username();
    }

    @Override
    public boolean isEnabled() {
        return user.enabled();
    }

    @Override
    public String getName() {
        return user.id().toString();
    }

    public String getDisplayName() {
        return user.displayName();
    }
}
