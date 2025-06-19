package com.example.application.security;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Service for retrieving the currently authenticated user from the Spring Security context.
 * <p>
 * This service provides methods to safely access user information stored in the authentication principal, supporting
 * principals that implement {@link AppUserPrincipal}. It serves as a bridge between Spring Security's authentication
 * model and the application's user information model.
 * </p>
 * <p>
 * Usage examples (assumes {@code currentUser} has been injected):
 *
 * <!-- spotless:off -->
 * <pre>
 * {@code
 * // Get the current user if available
 * Optional<AppUserInfo> currentUser = currentUser.get();
 *
 * // Get the current user, throwing an exception if not authenticated
 * AppUserInfo user = currentUser.require();
 *
 * // Access user properties
 * String fullName = currentUser.require().getFullName();
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 *
 * @see AppUserInfo The application's user information model
 * @see AppUserPrincipal The principal interface that provides access to user information
 */
public class CurrentUser {

    private static final Logger log = LoggerFactory.getLogger(CurrentUser.class);

    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    /**
     * Creates a new {@code CurrentUser} service for the given {@link SecurityContextHolderStrategy}.
     * <p>
     * This constructor uses the new Spring Security recommendation of accessing the
     * {@link SecurityContextHolderStrategy} as a bean rather than using the static methods of
     * {@link org.springframework.security.core.context.SecurityContextHolder}.
     * </p>
     *
     * @param securityContextHolderStrategy
     *            the strategy used to fetch the security context (never {@code null}).
     */
    CurrentUser(SecurityContextHolderStrategy securityContextHolderStrategy) {
        this.securityContextHolderStrategy = requireNonNull(securityContextHolderStrategy);
    }

    /**
     * Returns the currently authenticated user from the security context.
     * <p>
     * This method safely extracts user information from the current security context without throwing exceptions for
     * unauthenticated requests or incompatible principal types.
     * </p>
     * <p>
     * The method expects the authentication principal to implement {@link AppUserPrincipal}. If the principal doesn't
     * implement this interface, a warning is logged and an empty Optional is returned.
     * </p>
     *
     * @return an {@code Optional} containing the current user if authenticated and accessible, or an empty
     *         {@code Optional} if there is no authenticated user or the principal doesn't implement
     *         {@link AppUserPrincipal}
     * @see #require() For cases where authentication is required
     */
    public Optional<AppUserInfo> get() {
        return getPrincipal().map(AppUserPrincipal::getAppUser);
    }

    /**
     * Returns the currently authenticated principal from the security context.
     * <p>
     * This method safely extracts the principal from the current security context without throwing exceptions for
     * unauthenticated requests or incompatible principal types.
     * </p>
     * <p>
     * The method expects the authentication principal to implement {@link AppUserPrincipal}. If the principal doesn't
     * implement this interface, a warning is logged and an empty Optional is returned.
     * </p>
     *
     * @return an {@code Optional} containing the current principal if authenticated and accessible, or an empty
     *         {@code Optional} if there is no authenticated user or the principal doesn't implement
     *         {@link AppUserPrincipal}
     * @see #requirePrincipal() For cases where authentication is required
     */
    public Optional<AppUserPrincipal> getPrincipal() {
        return Optional.ofNullable(
                getPrincipalFromAuthentication(securityContextHolderStrategy.getContext().getAuthentication()));
    }

    /**
     * Extracts the principal from the provided authentication object.
     *
     * @param authentication
     *            the authentication object from which to extract the principal, may be {@code null}
     * @return the principal if available, or {@code null} if it cannot be extracted
     */
    private @Nullable AppUserPrincipal getPrincipalFromAuthentication(@Nullable Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof AppUserPrincipal appUserPrincipal) {
            return appUserPrincipal;
        }

        log.warn("Unexpected principal type: {}", principal.getClass().getName());

        return null;
    }

    /**
     * Returns the currently authenticated user from the security context.
     * <p>
     * Unlike {@link #get()}, this method throws an exception if no user is authenticated, making it suitable for
     * endpoints that require authentication.
     * </p>
     *
     * @return the currently authenticated user (never {@code null})
     * @throws AuthenticationCredentialsNotFoundException
     *             if there is no authenticated user, or the authenticated principal doesn't implement
     *             {@link AppUserPrincipal}
     */
    public AppUserInfo require() {
        return get().orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No current user"));
    }

    /**
     * Returns the currently authenticated principal from the security context.
     * <p>
     * Unlike {@link #getPrincipal()}, this method throws an exception if no user is authenticated, making it suitable
     * for endpoints that require authentication.
     * </p>
     *
     * @return the currently authenticated principal (never {@code null})
     * @throws AuthenticationCredentialsNotFoundException
     *             if there is no authenticated user, or the authenticated principal doesn't implement
     *             {@link AppUserPrincipal}
     */
    public AppUserPrincipal requirePrincipal() {
        return getPrincipal().orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No current user"));
    }
}
