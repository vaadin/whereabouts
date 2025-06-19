package com.example.application.security.dev;

import com.example.application.security.AppUserInfo;
import com.example.application.security.domain.UserId;
import org.jspecify.annotations.Nullable;

import java.time.ZoneId;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link AppUserInfo} used by {@link DevUser} for development environments.
 * <p>
 * This record provides a simple immutable implementation of the {@link AppUserInfo} interface, storing user information
 * for development and testing purposes. It contains all the essential user profile data needed by the application, with
 * appropriate null checks for required fields.
 * </p>
 * <p>
 * This implementation is specifically designed for development and test environments and should not be used in
 * production. It's primarily used by the {@link DevUser} class to represent test user information.
 * </p>
 *
 * @param userId
 *            the unique identifier for the user (never {@code null})
 * @param preferredUsername
 *            the user's preferred username (never {@code null}).
 * @param fullName
 *            the user's full name (never {@code null})
 * @param profileUrl
 *            the URL to the user's profile page, or {@code null} if not available
 * @param pictureUrl
 *            the URL to the user's profile picture, or {@code null} if not available
 * @param email
 *            the user's email address, or {@code null} if not available
 * @param zoneId
 *            the user's time zone (never {@code null})
 * @param locale
 *            the user's locale (never {@code null})
 * @see DevUser The development user class that uses this record
 * @see AppUserInfo The interface this record implements
 */
record DevUserInfo(UserId userId, String preferredUsername, String fullName, @Nullable String profileUrl,
        @Nullable String pictureUrl, @Nullable String email, ZoneId zoneId, Locale locale) implements AppUserInfo {

    DevUserInfo {
        requireNonNull(userId);
        requireNonNull(preferredUsername);
        requireNonNull(fullName);
        requireNonNull(zoneId);
        requireNonNull(locale);
    }

    @Override
    public UserId getUserId() {
        return userId;
    }

    @Override
    public String getPreferredUsername() {
        return preferredUsername;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public @Nullable String getProfileUrl() {
        return profileUrl;
    }

    @Override
    public @Nullable String getPictureUrl() {
        return pictureUrl;
    }

    @Override
    public @Nullable String getEmail() {
        return email;
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
