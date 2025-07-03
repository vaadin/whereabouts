package com.example.application.security.controlcenter;

import com.example.application.security.AppUserInfo;
import com.example.application.security.AppUserInfoLookup;
import com.example.application.security.domain.UserId;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.NotFoundException;
import org.jspecify.annotations.Nullable;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * For this to work, the application's client account must:
 * <ul>
 * <li>Be a <strong>service account</strong></li>
 * <li>Have the following service account roles: {@code view-users}, {@code query-users}</li>
 * </ul>
 */
class KeycloakAppUserInfoLookup implements AppUserInfoLookup, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAppUserInfoLookup.class);

    private final Keycloak keycloak;
    private final KeycloakCredentials credentials;

    KeycloakAppUserInfoLookup(KeycloakCredentials credentials) {
        requireNonNull(credentials, "credentials must not be null");
        log.info("Looking up users from serverUrl '{}' and realm '{}' using clientId '{}'", credentials.serverUrl,
                credentials.realm, credentials.clientId);
        keycloak = KeycloakBuilder.builder().serverUrl(credentials.serverUrl).realm(credentials.realm)
                .grantType("client_credentials").clientId(credentials.clientId).clientSecret(credentials.clientSecret)
                .build();
        this.credentials = credentials;
    }

    @Override
    public Optional<AppUserInfo> findUserInfo(UserId userId) {
        try {
            log.debug("Looking up user info for userId: {}", userId);
            var user = keycloak.realm(credentials.realm).users().get(userId.toString()).toRepresentation();
            return Optional.of(new KeycloakAppUserInfo(user));
        } catch (NotFoundException ex) {
            log.debug("User not found in Keycloak: {}", userId);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to lookup user info for userId: {}", userId, ex);
            throw new RuntimeException("Failed to retrieve user information from Keycloak", ex);
        }
    }

    @Override
    public List<AppUserInfo> findUsers(String searchTerm, int limit, int offset) {
        log.debug("Looking up users from searchTerm: {} (limit: {}, offset: {})", searchTerm, limit, offset);
        var users = keycloak.realm(credentials.realm).users().search(searchTerm, offset, limit);
        try {
            log.debug("Found {} users from Keycloak", users.size());
            return users.stream().map(KeycloakAppUserInfo::new).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Failed to lookup users from searchTerm: {}", searchTerm, ex);
            throw new RuntimeException("Failed to retrieve users from Keycloak", ex);
        }
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        keycloak.close();
    }

    record KeycloakCredentials(String serverUrl, String realm, String clientId, String clientSecret) {
        KeycloakCredentials {
            requireNonNull(serverUrl, "serverUrl must not be null");
            requireNonNull(realm, "realm must not be null");
            requireNonNull(clientId, "clientId must not be null");
            requireNonNull(clientSecret, "clientSecret must not be null");
        }
    }

    static KeycloakCredentials createCredentials(String oidcIssuerUri, String clientId, String clientSecret) {
        requireNonNull(oidcIssuerUri, "oidcIssuerUri must not be null");
        requireNonNull(clientId, "clientId must not be null");
        requireNonNull(clientSecret, "clientSecret must not be null");

        var realmsIndex = oidcIssuerUri.indexOf("/realms/");
        if (realmsIndex < 0) {
            throw new IllegalArgumentException("OIDC issuer does not appear to be Keycloak: " + oidcIssuerUri);
        }

        var serverUrl = oidcIssuerUri.substring(0, realmsIndex);
        var realmStart = realmsIndex + "/realms/".length();
        var realmEnd = oidcIssuerUri.indexOf("/", realmStart);
        if (realmEnd < 0) {
            realmEnd = oidcIssuerUri.length();
        }

        var realm = oidcIssuerUri.substring(realmStart, realmEnd);
        if (realm.isEmpty()) {
            throw new IllegalArgumentException("Realm name is empty in OIDC issuer: " + oidcIssuerUri);
        }

        return new KeycloakCredentials(serverUrl, realm, clientId, clientSecret);
    }

    private static class KeycloakAppUserInfo implements AppUserInfo {

        private final UserId userId;
        private final String preferredUsername;
        private final String fullName;
        private final String email;
        private final String profileUrl;
        private final String pictureUrl;
        private final ZoneId zoneId;
        private final Locale locale;

        KeycloakAppUserInfo(UserRepresentation user) {
            userId = UserId.of(user.getId());
            preferredUsername = requireNonNull(user.getUsername());
            fullName = buildFullName(user);
            email = user.getEmail();
            profileUrl = user.firstAttribute(StandardClaimNames.PROFILE);
            pictureUrl = user.firstAttribute(StandardClaimNames.PICTURE);
            zoneId = OidcUserAdapter.parseZoneInfo(user.firstAttribute(StandardClaimNames.ZONEINFO));
            locale = OidcUserAdapter.parseLocale(user.firstAttribute(StandardClaimNames.LOCALE));
        }

        private static String buildFullName(UserRepresentation user) {
            var firstName = user.getFirstName();
            var lastName = user.getLastName();

            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (lastName != null) {
                return lastName;
            } else {
                return user.getUsername(); // Fallback to username
            }
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
        public @Nullable String getEmail() {
            return email;
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
        public ZoneId getZoneId() {
            return zoneId;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }
    }
}
