package com.example.application.security.controlcenter;

import com.vaadin.controlcenter.starter.idm.IdentityManagementConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.StringUtils;

/**
 * Security configuration for production environments where the application is deployed and managed by Vaadin Control
 * Center (CC).
 *
 * <p>
 * This configuration is specifically designed for Kubernetes deployments and integrates with Vaadin Control Center's
 * Identity Management system using OIDC (OpenID Connect) and Keycloak as the identity provider.
 * </p>
 *
 * <p>
 * The configuration is conditionally activated when the application is deployed on the Kubernetes platform.
 * </p>
 *
 * <p>
 * This class extends {@link IdentityManagementConfiguration} to leverage Control Center's built-in identity management
 * capabilities while customizing OIDC user mapping to use the application's own security model.
 * </p>
 *
 * @see IdentityManagementConfiguration
 */
@EnableWebSecurity
@Configuration
@ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
public class ControlCenterSecurityConfig extends IdentityManagementConfiguration {

    /**
     * Creates and configures an OIDC user service with custom user mapping for the application.
     *
     * <p>
     * This bean configures how Spring Security should load and map OIDC user information after successful
     * authentication. The service uses a custom mapper that transforms the standard OIDC user data from Keycloak into
     * the application's own security model via {@link OidcUserAdapter}.
     * </p>
     *
     * <p>
     * Spring Security will use this service during the OIDC authentication flow to create application-specific user
     * objects from the authenticated OIDC user information.
     * </p>
     *
     * @return configured {@link OidcUserService} instance with custom user mapping
     * @see OidcUserService
     * @see #mapOidcUser(OidcUserRequest, OidcUserInfo)
     */
    @Bean
    OidcUserService oidcUserService() {
        var userService = new OidcUserService();
        userService.setOidcUserMapper(ControlCenterSecurityConfig::mapOidcUser);
        return userService;
    }

    /**
     * Maps OIDC user request and user info to an application-specific {@link OidcUserAdapter} instance.
     *
     * <p>
     * This method transforms the raw OIDC user information received from Keycloak into the application's own security
     * model by first creating a standard {@link DefaultOidcUser} and then wrapping it in an {@link OidcUserAdapter}
     * that adapts the OIDC user to the application's security model.
     * </p>
     * <p>
     * This adapter pattern allows the application to:
     * <ul>
     * <li>Use consistent security expressions like {@code authentication.principal.appUser.userId}</li>
     * <li>Avoid method name conflicts between OIDC interfaces and the application's user model</li>
     * <li>Provide a uniform interface for all authentication mechanisms</li>
     * </ul>
     * </p>
     * <p>
     * The mapping process:
     * </p>
     * <ol>
     * <li>Extracts and maps user authorities from the OIDC user request and info</li>
     * <li>Retrieves the username attribute name from the provider configuration</li>
     * <li>Creates a {@link DefaultOidcUser} with the mapped authorities, ID token, user info, and optionally the
     * username attribute name</li>
     * <li>Wraps the {@link DefaultOidcUser} in an application-specific {@link OidcUserAdapter} that bridges OIDC
     * authentication with the application's security model</li>
     * </ol>
     *
     * @param userRequest
     *            the OIDC user request containing client registration and tokens
     * @param userInfo
     *            the OIDC user information containing user claims and attributes
     * @return an {@link OidcUserAdapter} instance that adapts the OIDC user to the application's security model
     * @throws IllegalArgumentException
     *             if userRequest or userInfo is null
     * @see DefaultOidcUser
     * @see OidcUserAdapter
     * @see #mapAuthorities(OidcUserRequest, OidcUserInfo)
     */
    private static OidcUser mapOidcUser(OidcUserRequest userRequest, OidcUserInfo userInfo) {
        var authorities = mapAuthorities(userRequest, userInfo);
        var providerDetails = userRequest.getClientRegistration().getProviderDetails();
        var userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        var oidcUser = StringUtils.hasText(userNameAttributeName)
                ? new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName)
                : new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
        return new OidcUserAdapter(oidcUser);
    }
}
