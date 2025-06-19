package com.example.application.security.dev;

import com.example.application.security.AppUserInfo;
import com.example.application.security.AppUserPrincipal;
import com.example.application.security.domain.UserId;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link AppUserPrincipal} and {@link UserDetails} for development environments.
 * <p>
 * This class provides a convenient way to create test users with predefined credentials and user information for
 * development and testing purposes. It implements both Spring Security's {@link UserDetails} interface and the
 * application's {@link AppUserPrincipal} interface, making it compatible with both authentication and authorization
 * systems.
 * </p>
 * <p>
 * DevUser instances should only be used in development and test environments, not in production. They are typically
 * created through the {@link #builder()} method and customized using the fluent {@link DevUserBuilder} API.
 * </p>
 * <p>
 * Example usage: <!-- spotless:off -->
 * <pre>
 * {@code
 * DevUser adminUser = DevUser.builder()
 *     .preferredUsername("admin")
 *     .fullName("Admin User")
 *     .password("securePassword")
 *     .email("admin@example.com")
 *     .roles("ADMIN")
 *     .build();
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 *
 * @see DevUserBuilder The builder for creating DevUser instances
 * @see AppUserPrincipal The principal interface providing application user information
 * @see UserDetails Spring Security's interface for user authentication information
 */
final class DevUser implements AppUserPrincipal, UserDetails {

    private final AppUserInfo appUser;
    private final Set<GrantedAuthority> authorities;
    private final String password;

    DevUser(AppUserInfo appUser, Collection<GrantedAuthority> authorities, String password) {
        this.appUser = requireNonNull(appUser);
        this.authorities = Set.copyOf(authorities);
        this.password = requireNonNull(password);
    }

    @Override
    public AppUserInfo getAppUser() {
        return appUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return appUser.getPreferredUsername();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DevUser user) {
            return this.appUser.getUserId().equals(user.appUser.getUserId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.appUser.getUserId().hashCode();
    }

    /**
     * Creates a new builder for constructing a development user.
     * <p>
     * The builder provides a fluent API for setting user properties. At minimum, a preferred username and password must
     * be provided before calling {@link DevUserBuilder#build()}.
     * </p>
     *
     * @return a new builder for creating a development user
     */
    public static DevUserBuilder builder() {
        return new DevUserBuilder();
    }

    /**
     * Builder for creating {@link DevUser} instances with customized properties.
     * <p>
     * This builder uses a fluent API pattern to set various user properties before constructing the final user object.
     * </p>
     * <p>
     * Required properties that must be set before calling {@link #build()}:
     * <ul>
     * <li>Preferred username (set via {@link #preferredUsername(String)})</li>
     * <li>Password (set via {@link #password(String)})</li>
     * </ul>
     * </p>
     * <p>
     * Optional properties with default values:
     * <ul>
     * <li>User ID (random UUID)</li>
     * <li>Full name (preferred username)</li>
     * <li>Zone ID (system default)</li>
     * <li>Locale (system default)</li>
     * <li>Authorities (empty list)</li>
     * </ul>
     * </p>
     * <p>
     * Example usage:
     * 
     * <pre>
     * {@code
     * DevUser user = DevUser.builder().preferredUsername("john.doe").fullName("John Doe").password("password123")
     *         .email("john@example.com").roles("USER", "ADMIN").locale(Locale.US).build();
     * }
     * </pre>
     * </p>
     */
    static final class DevUserBuilder {

        private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories
                .createDelegatingPasswordEncoder();

        private @Nullable UserId userId;
        private @Nullable String preferredUsername;
        private @Nullable String fullName;
        private @Nullable String email;
        private @Nullable String profileUrl;
        private @Nullable String pictureUrl;
        private ZoneId zoneInfo = ZoneId.systemDefault();
        private Locale locale = Locale.getDefault();
        private List<GrantedAuthority> authorities = Collections.emptyList();
        private @Nullable String password;

        /**
         * Sets the user's ID. If left unset, a random UUID is generated when the user is built.
         *
         * @param userId
         *            the user ID (never {@code null})
         * @return this builder for method chaining
         */
        public DevUserBuilder userId(UserId userId) {
            this.userId = requireNonNull(userId);
            return this;
        }

        /**
         * Sets the user's preferred username.
         *
         * @param preferredUsername
         *            the preferred username (never {@code null})
         * @return this builder for method chaining
         */
        public DevUserBuilder preferredUsername(String preferredUsername) {
            this.preferredUsername = requireNonNull(preferredUsername);
            return this;
        }

        /**
         * Sets the user's full name. If left unset, the preferred username will be used.
         *
         * @param fullName
         *            the full name
         * @return this builder for method chaining
         */
        public DevUserBuilder fullName(@Nullable String fullName) {
            this.fullName = fullName;
            return this;
        }

        /**
         * Sets the user's email address.
         *
         * @param email
         *            the email address.
         * @return this builder for method chaining.
         */
        public DevUserBuilder email(@Nullable String email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the user's profile URL.
         *
         * @param profileUrl
         *            the profile URL
         * @return this builder for method chaining
         */
        public DevUserBuilder profileUrl(@Nullable String profileUrl) {
            this.profileUrl = profileUrl;
            return this;
        }

        /**
         * Sets the user's profile picture URL.
         *
         * @param pictureUrl
         *            the picture URL
         * @return this builder for method chaining
         */
        public DevUserBuilder pictureUrl(@Nullable String pictureUrl) {
            this.pictureUrl = pictureUrl;
            return this;
        }

        /**
         * Sets the user's time zone.
         *
         * @param zoneInfo
         *            the time zone (never {@code null})
         * @return this builder for method chaining
         */
        public DevUserBuilder zoneInfo(ZoneId zoneInfo) {
            this.zoneInfo = requireNonNull(zoneInfo);
            return this;
        }

        /**
         * Sets the user's locale.
         *
         * @param locale
         *            the locale (never {@code null})
         * @return this builder for method chaining
         */
        public DevUserBuilder locale(Locale locale) {
            this.locale = requireNonNull(locale);
            return this;
        }

        /**
         * Sets the user's roles, which will be converted to authorities with the "ROLE_" prefix.
         * <p>
         * For example, the role "ADMIN" will become the authority "ROLE_ADMIN".
         * </p>
         *
         * @param roles
         *            the roles to assign to the user
         * @return this builder for method chaining
         */
        public DevUserBuilder roles(String... roles) {
            this.authorities = new ArrayList<>(roles.length);
            for (var role : roles) {
                this.authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return this;
        }

        /**
         * Sets the user's authorities directly.
         *
         * @param authorities
         *            the authority strings
         * @return this builder for method chaining
         */
        public DevUserBuilder authorities(String... authorities) {
            return authorities(AuthorityUtils.createAuthorityList(authorities));
        }

        /**
         * Sets the user's authorities directly.
         *
         * @param authorities
         *            the authority objects (never {@code null})
         * @return this builder for method chaining
         */
        public DevUserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<>(authorities);
            return this;
        }

        /**
         * Sets the user's password.
         * <p>
         * The password will be encoded when the user is built.
         * </p>
         *
         * @param password
         *            the raw password (never {@code null})
         * @return this builder for method chaining
         */
        public DevUserBuilder password(String password) {
            this.password = requireNonNull(password);
            return this;
        }

        /**
         * Builds the development user with the properties set on this builder.
         *
         * @return a new development user
         * @throws IllegalStateException
         *             if the preferred username or password has not been set
         */
        public DevUser build() {
            if (preferredUsername == null) {
                throw new IllegalStateException("Preferred username must be set before building the user");
            }
            if (password == null) {
                throw new IllegalStateException("Password must be set before building the user");
            }
            var encodedPassword = PASSWORD_ENCODER.encode(password);
            if (userId == null) {
                userId = UserId.of(UUID.randomUUID().toString());
            }
            if (fullName == null) {
                fullName = preferredUsername;
            }
            return new DevUser(new DevUserInfo(userId, preferredUsername, fullName, profileUrl, pictureUrl, email,
                    zoneInfo, locale), authorities, encodedPassword);
        }
    }
}
