package com.example.application.security;

import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

/**
 * Interface for principals that provide access to application user information.
 * <p>
 * This interface extends {@link Principal} to ensure it can be used as a security principal while providing consistent
 * access to the application's user information model.
 * </p>
 * <p>
 * All authenticated principals in the application should implement this interface, allowing security expressions to
 * access user information with a consistent pattern: {@code authentication.principal.appUser.userId}
 * </p>
 */
public interface AppUserPrincipal extends Principal {

    /**
     * Returns the application's user information.
     * <p>
     * This method provides consistent access to the application's user model regardless of the authentication
     * mechanism.
     * </p>
     *
     * @return the application user information (never {@code null})
     */
    AppUserInfo getAppUser();

    /**
     * Returns the name of the principal, which is the user ID as a string.
     * <p>
     * This default implementation uses the user ID from {@link #getAppUser()} as the principal name, ensuring a
     * consistent, unique identifier across all authentication mechanisms.
     * </p>
     *
     * @return the principal name (never {@code null})
     */
    @Override
    default String getName() {
        return getAppUser().getUserId().toString();
    }

    /**
     * Returns the authorities of the principal.
     *
     * @return an unmodifiable collection of granted authorities (never {@code null})
     */
    Collection<? extends GrantedAuthority> getAuthorities();
}
