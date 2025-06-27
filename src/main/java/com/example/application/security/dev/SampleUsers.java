package com.example.application.security.dev;

import com.example.application.security.AppRoles;
import com.example.application.security.domain.UserId;

import java.util.List;
import java.util.UUID;

/**
 * Provides predefined sample users for development and testing environments.
 * <p>
 * This utility class contains constants and user objects that can be used consistently across development
 * configurations, integration tests, and other testing scenarios. It ensures that the same test users are available in
 * all contexts where they are needed, promoting consistency and reducing duplication.
 * </p>
 * <p>
 * The class provides both the complete {@link DevUser} objects (for use in user details services) and individual
 * constants for user IDs and preferred usernames (for use in tests and assertions). This allows tests to reference
 * specific user properties without needing to extract them from the user objects.
 * </p>
 * <p>
 * Usage in development configuration: <!-- spotless:off -->
 * <pre>
 * {@code
 * @Bean
 * UserDetailsService userDetailsService() {
 *     return new DevUserDetailsService(SampleUsers.ADMIN, SampleUsers.USER);
 * }
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 * <p>
 * Usage in integration tests: <!-- spotless:off -->
 * <pre>
 * {@code
 * @Test
 * @WithUserDetails(SampleUsers.USER_USERNAME)
 * public void testUserFunctionality() {
 *     // Test logic here
 *     assertThat(result.getCreatedBy()).isEqualTo(SampleUsers.USER_ID);
 * }
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 * <p>
 * <strong>Important:</strong> This class is intended only for development and testing purposes. The sample users have
 * fixed, well-known credentials and should never be used in production environments.
 * </p>
 *
 * @see DevUser The development user implementation
 * @see DevUserDetailsService The service that uses these sample users
 */
public final class SampleUsers {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SampleUsers() {
    }

    /**
     * The raw, unencoded password used by all sample users.
     */
    static final String SAMPLE_PASSWORD = "123";

    /**
     * The user ID for the admin sample user.
     * <p>
     * This constant can be used in tests and assertions to verify that operations were performed by the admin user.
     * </p>
     */
    public static final UserId ADMIN_ID = UserId.of(UUID.randomUUID().toString());

    /**
     * The preferred username of the admin sample user.
     * <p>
     * This constant can be used with {@code @WithUserDetails} in tests to authenticate as the admin user.
     * </p>
     */
    public static final String ADMIN_USERNAME = "admin";

    /**
     * The admin sample user with administrative privileges.
     * <p>
     * This user has both the "ADMIN" and the "USER" role and can be used in development configurations and tests that
     * require administrative access.
     * </p>
     */
    static DevUser ADMIN = DevUser.builder().preferredUsername(ADMIN_USERNAME).fullName("Alice Administrator")
            .userId(ADMIN_ID).password(SAMPLE_PASSWORD).email("alice@example.com").roles(AppRoles.ADMIN, AppRoles.USER)
            .build();

    /**
     * The user ID for the regular sample user.
     * <p>
     * This constant can be used in tests and assertions to verify that operations were performed by the regular user.
     * </p>
     */
    public static final UserId USER_ID = UserId.of(UUID.randomUUID().toString());

    /**
     * The preferred username of the regular sample user.
     * <p>
     * This constant can be used with {@code @WithUserDetails} in tests to authenticate as the regular user.
     * </p>
     */
    public static final String USER_USERNAME = "user";

    /**
     * The regular sample user with standard privileges.
     * <p>
     * This user has the "USER" role and can be used in development configurations and tests that require standard user
     * access.
     * </p>
     */
    static final DevUser USER = DevUser.builder().preferredUsername(USER_USERNAME).fullName("Ursula User")
            .userId(USER_ID).password(SAMPLE_PASSWORD).email("ursula@example.com").roles(AppRoles.USER).build();

    /**
     * An unmodifiable list containing all sample users.
     * <p>
     * This list provides a convenient way to access all sample users at once, which is particularly useful when
     * creating a {@link DevUserDetailsService} that should include all available test users. Using this list ensures
     * that any new sample users added to this class will automatically be included in services that use it.
     * </p>
     * <p>
     * Example usage in development configuration: <!-- spotless:off -->
     * <pre>
     * {@code
     * @Bean
     * UserDetailsService userDetailsService() {
     *     return new DevUserDetailsService(SampleUsers.ALL_USERS);
     * }
     * }
     * </pre>
     * <!-- spotless:on -->
     * </p>
     */
    static final List<DevUser> ALL_USERS = List.of(USER, ADMIN);
}
