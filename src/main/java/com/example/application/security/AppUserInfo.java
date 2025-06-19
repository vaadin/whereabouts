/**
 * This package contains an initial security configuration for your application.
 * <p>
 * It provides the following features:
 * <ul>
 * <li>An application-specific user model for accessing user information regardless of the underlying identity
 * management implementation ({@link com.example.application.security.AppUserInfo},
 * {@link com.example.application.security.CurrentUser})</li>
 * <li>A value object for identifying users ({@link com.example.application.security.domain.UserId})</li>
 * <li>Method-level security and JPA auditing ({@link com.example.application.security.CommonSecurityConfig})</li>
 * <li>A development mode security configuration with simple login and in-memory users ({@code dev} package)</li>
 * <li>A production mode security configuration for use with Vaadin Control Center ({@code cc} package)</li>
 * </ul>
 * </p>
 * <p>
 * You can use the package as-is in your application or extend and modify it to your needs. If you want to build your
 * own security model from scratch, delete the package.
 * </p>
 */
package com.example.application.security;

import com.example.application.security.domain.UserId;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;
import java.util.Locale;

/**
 * Interface for accessing information about an application user.
 * <p>
 * This interface provides standard methods to access user identity and profile information throughout the application.
 * It can be used in various contexts, both for retrieving the current authenticated user and for accessing information
 * about any user in the system (e.g., when displaying audit information about who last modified a record).
 * </p>
 * <p>
 * Note: This interface intentionally does not extend {@link org.springframework.security.core.userdetails.UserDetails
 * UserDetails} or {@link org.springframework.security.core.AuthenticatedPrincipal AuthenticatedPrincipal} to maintain
 * separation between authentication concerns and general user information access. For the same reason, it does not
 * contain information about the user's roles or authorities.
 * </p>
 */
public interface AppUserInfo {

    /**
     * Returns the user's unique identifier within the application.
     * <p>
     * For OIDC authenticated users, this typically corresponds to the "subject" claim. This identifier remains
     * consistent across sessions and is used as the primary key for user-related data.
     * </p>
     *
     * @return the unique user identifier (never {@code null})
     */
    UserId getUserId();

    /**
     * Returns the user's preferred username for display and identification purposes.
     * <p>
     * This username is intended for human-readable display in user interfaces and may be different from the unique
     * {@link #getUserId()}. Unlike the user ID, the preferred username is typically chosen by the user and may be more
     * meaningful to them and other users of the application.
     * </p>
     * <p>
     * <strong>Important:</strong> The preferred username can change over time as users update their profiles. It should
     * <strong>not</strong> be used as a permanent identifier for users in database relationships, audit logs, or any
     * other persistent storage. Use {@link #getUserId()} for permanent user identification. The preferred username
     * should only be used for identification when entered by a human user (e.g., in search forms or user lookup
     * interfaces).
     * </p>
     * <p>
     * For OIDC authenticated users, this typically corresponds to the "preferred_username" claim.
     * </p>
     *
     * @return the user's preferred username (never {@code null})
     * @see #getUserId() For permanent, immutable user identification
     */
    String getPreferredUsername();

    /**
     * Returns the user's full display name.
     * <p>
     * This typically combines the user's first and last name in a format appropriate for display in the user interface.
     * If the user has no full name, the preferred username is used instead.
     * </p>
     *
     * @return the user's full name (never {@code null})
     */
    default String getFullName() {
        return getPreferredUsername();
    }

    /**
     * Returns a URL to the user's profile page in the application or external system.
     * <p>
     * Implementations may return {@code null} if no profile page is available or if the current context doesn't have
     * permission to access this information.
     * </p>
     *
     * @return URL to the user's profile, or {@code null} if not available
     */
    default @Nullable String getProfileUrl() {
        return null;
    }

    /**
     * Returns a URL to the user's profile picture or avatar.
     * <p>
     * Implementations may return {@code null} if no picture is available or if the current context doesn't have
     * permission to access this information.
     * </p>
     *
     * @return URL to the user's picture, or {@code null} if not available
     */
    default @Nullable String getPictureUrl() {
        return null;
    }

    /**
     * Returns the user's email address.
     * <p>
     * This email address is considered the primary contact method for the user and may be used for notifications and
     * communications.
     * </p>
     *
     * @return the user's email address, or {@code null} if not available
     */
    default @Nullable String getEmail() {
        return null;
    }

    /**
     * Returns the user's preferred time zone.
     * <p>
     * This time zone is used for displaying dates and times in the user interface. If the user has not explicitly set a
     * time zone preference, the system default time zone is returned as a fallback.
     * </p>
     *
     * @return the user's time zone (never {@code null})
     */
    default ZoneId getZoneId() {
        return ZoneId.systemDefault();
    }

    /**
     * Returns the user's preferred locale for internationalization.
     * <p>
     * This locale is used for language selection and formatting of numbers, dates, and currencies in the user
     * interface. If the user has not explicitly set a locale preference, the system default locale is returned as a
     * fallback.
     * </p>
     *
     * @return the user's locale (never {@code null})
     */
    default Locale getLocale() {
        return Locale.getDefault();
    }
}
