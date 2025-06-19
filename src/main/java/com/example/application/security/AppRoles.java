package com.example.application.security;

/**
 * Constants for application role names used throughout the security system.
 * <p>
 * This class centralizes the definition of all role names used in the application, ensuring consistency across security
 * configurations, annotations, and tests. Using these constants instead of string literals helps prevent typos and
 * makes role management more maintainable.
 * </p>
 * <p>
 * These role names are used in various contexts:
 * <ul>
 * <li>Security method annotations: {@code @PreAuthorize("hasRole('" + AppRoles.ADMIN + "')")}</li>
 * <li>Security configuration and access control rules</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Customization:</strong> Modify or add role constants in this class to match your application's security
 * requirements. Consider the principle of least privilege when defining roles and their associated permissions.
 * </p>
 * <p>
 * Example usage: <!-- spotless:off -->
 * <pre>
 * {@code
 * // In security annotations
 * @PreAuthorize("hasRole('" + AppRoles.ADMIN + "')")
 * public void adminOnlyMethod() { ... }
 *
 * @Route
 * @RolesAllowed(AppRoles.ADMIN)
 * public class AdminView extends Main { ... }
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 *
 * @see org.springframework.security.access.prepost.PreAuthorize
 */
public final class AppRoles {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AppRoles() {
    }

    /**
     * Role for administrative users with elevated privileges.
     * <p>
     * Users with this role typically have access to administrative functions such as user management, system
     * configuration, and sensitive operations. Use this role sparingly and only for users who require administrative
     * access.
     * </p>
     */
    public static final String ADMIN = "ADMIN";

    /**
     * Role for standard application users.
     * <p>
     * This is the default role for regular users of the application. Users with this role have access to standard
     * application features but not administrative functions.
     * </p>
     */
    public static final String USER = "USER";
}
