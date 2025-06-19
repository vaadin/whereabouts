package com.example.application.security.domain;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Value object representing a user's unique identifier.
 * <p>
 * This class encapsulates a user ID string value, providing type safety and validation. It follows the domain primitive
 * pattern, ensuring that all user IDs in the application are validated consistently and can be used as immutable value
 * objects.
 * </p>
 * <p>
 * UserId objects are immutable, serializable, and implement proper equality semantics. They should be used throughout
 * the application wherever a user ID is needed, rather than using raw strings.
 * </p>
 * <p>
 * Example usage: <!-- spotless:off -->
 * <pre>
 * {@code
 * // Creating a UserId
 * UserId userId = UserId.of("user-123");
 *
 * // Using it in a method
 * Optional<AppUserInfo> userInfo = userService.findUserInfo(userId);
 *
 * // Converting back to string when necessary
 * String userIdString = userId.toString();
 * }
 * </pre>
 * <!-- spotless:on -->
 * </p>
 */
public final class UserId implements Serializable {

    private final String userId;

    private UserId(String userId) {
        // TODO If the userId has a specific format, validate it here.
        this.userId = requireNonNull(userId);
    }

    /**
     * Creates a new {@code UserId} instance with the specified value.
     * <p>
     * This factory method is the recommended way to create UserId objects. It ensures proper validation and
     * encapsulation of the user ID value.
     * </p>
     *
     * @param userId
     *            the user ID string (never {@code null})
     * @return a new {@code UserId} instance
     * @throws IllegalArgumentException
     *             if the user ID is invalid
     */
    public static UserId of(String userId) {
        return new UserId(userId);
    }

    /**
     * Returns the string representation of this user ID.
     * <p>
     * This method returns the original user ID string that was provided when this object was created.
     * </p>
     *
     * @return the user ID as a string
     */
    @Override
    public String toString() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        UserId that = (UserId) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
