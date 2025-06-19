package com.example.application.security.controlcenter;

import com.example.application.security.AppUserInfo;
import com.example.application.security.AppUserPrincipal;
import com.example.application.security.domain.UserId;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Adapter implementation that bridges Spring Security's OIDC user representation with the application's
 * {@link AppUserInfo} interface.
 * <p>
 * This class wraps an {@link OidcUser} and implements {@link AppUserPrincipal} to provide consistent access to user
 * information throughout the application without method conflicts.
 * </p>
 */
final class OidcUserAdapter implements OidcUser, AppUserPrincipal {

    private final OidcUser delegate;
    private final AppUserInfo appUserInfo;

    /**
     * Creates a new adapter for the specified OIDC user.
     *
     * @param oidcUser
     *            the OIDC user to adapt (never {@code null})
     */
    public OidcUserAdapter(OidcUser oidcUser) {
        this.delegate = requireNonNull(oidcUser);
        this.appUserInfo = createAppUserInfo(oidcUser);
    }

    private static AppUserInfo createAppUserInfo(OidcUser oidcUser) {
        return new AppUserInfo() {
            private final UserId userId = UserId.of(oidcUser.getSubject());
            private final String preferredUsername = requireNonNull(oidcUser.getPreferredUsername());
            private final String fullName = requireNonNull(oidcUser.getFullName());
            private final ZoneId zoneId = parseZoneInfo(oidcUser.getZoneInfo());
            private final Locale locale = parseLocale(oidcUser.getLocale());

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
                return oidcUser.getProfile();
            }

            @Override
            public @Nullable String getPictureUrl() {
                return oidcUser.getPicture();
            }

            @Override
            public @Nullable String getEmail() {
                return oidcUser.getEmail();
            }

            @Override
            public ZoneId getZoneId() {
                return zoneId;
            }

            @Override
            public Locale getLocale() {
                return locale;
            }
        };
    }

    /**
     * Parses a zone info string into a {@link ZoneId}, with fallback to system default.
     * <p>
     * This utility method safely converts OIDC "zoneinfo" claim values or similar timezone identifiers into Java
     * {@link ZoneId} objects. It handles invalid or null input gracefully by falling back to the system default
     * timezone.
     * </p>
     * <p>
     * The method accepts standard timezone identifiers such as:
     * <ul>
     * <li>"America/New_York"</li>
     * <li>"Europe/London"</li>
     * <li>"UTC"</li>
     * <li>"+02:00" (offset-based IDs)</li>
     * </ul>
     * </p>
     *
     * @param zoneInfo
     *            the timezone identifier string, may be {@code null}
     * @return a {@link ZoneId} parsed from the input, or the system default if the input is null or invalid
     * @see ZoneId#of(String) The underlying parsing method
     * @see ZoneId#systemDefault() The fallback used for invalid input
     */
    static ZoneId parseZoneInfo(@Nullable String zoneInfo) {
        if (zoneInfo == null) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(zoneInfo);
        } catch (DateTimeException e) {
            return ZoneId.systemDefault();
        }
    }

    /**
     * Parses a locale string into a {@link Locale}, with fallback to system default.
     * <p>
     * This utility method safely converts OIDC "locale" claim values or similar locale identifiers into Java
     * {@link Locale} objects. It handles null input by falling back to the system default locale.
     * </p>
     * <p>
     * The method accepts standard locale tags such as:
     * <ul>
     * <li>"en-US" (English, United States)</li>
     * <li>"fr-FR" (French, France)</li>
     * <li>"en" (English, no specific region)</li>
     * <li>"zh-Hans-CN" (Chinese, Simplified script, China)</li>
     * </ul>
     * </p>
     * <p>
     * Unlike {@link #parseZoneInfo(String)}, this method does not catch parsing exceptions, as
     * {@link Locale#forLanguageTag(String)} handles malformed tags gracefully by returning a best-effort locale or the
     * root locale.
     * </p>
     *
     * @param locale
     *            the locale identifier string (BCP 47 language tag), may be {@code null}
     * @return a {@link Locale} parsed from the input, or the system default if the input is null
     * @see Locale#forLanguageTag(String) The underlying parsing method
     * @see Locale#getDefault() The fallback used for null input
     */
    static Locale parseLocale(@Nullable String locale) {
        if (locale == null) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(locale);
    }

    @Override
    public AppUserInfo getAppUser() {
        return appUserInfo;
    }

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
