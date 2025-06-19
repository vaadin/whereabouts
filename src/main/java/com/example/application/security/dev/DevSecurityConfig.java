package com.example.application.security.dev;

import com.example.application.security.controlcenter.ControlCenterSecurityConfig;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.security.VaadinAwareSecurityContextHolderStrategyConfiguration;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the development environment.
 * <p>
 * This configuration simplifies authentication during development by:
 * <ul>
 * <li>Using a simple login view for authentication</li>
 * <li>Providing predefined test users with fixed credentials</li>
 * <li>Using an in-memory user details service with no external dependencies</li>
 * </ul>
 * </p>
 * <p>
 * This configuration is automatically activated when {@link ControlCenterSecurityConfig} is not active. It should
 * <strong>not</strong> be used in production environments, as it uses hardcoded credentials and simplified security
 * settings.
 * </p>
 * <p>
 * The predefined users are declared in the {@link SampleUsers} class.
 * </p>
 * <p>
 * This configuration integrates with Vaadin's security framework through {@link VaadinSecurityConfigurer} to provide a
 * seamless login experience in the Vaadin UI.
 * </p>
 *
 * @see DevUserDetailsService The in-memory user details service implementation
 * @see DevUser Builder for creating development test users
 * @see SampleUsers User credentials for the predefined users
 */
@EnableWebSecurity
@Configuration
@Import({ VaadinAwareSecurityContextHolderStrategyConfiguration.class })
@ConditionalOnMissingBean(ControlCenterSecurityConfig.class)
class DevSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(DevSecurityConfig.class);

    DevSecurityConfig() {
        log.warn("Using DEVELOPMENT security configuration. This should not be used in production environments!");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer.loginView(DevLoginView.LOGIN_PATH))
                .build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new DevUserDetailsService(SampleUsers.ALL_USERS);
    }

    @Bean
    VaadinServiceInitListener developmentLoginConfigurer() {
        return (serviceInitEvent) -> {
            if (serviceInitEvent.getSource().getDeploymentConfiguration().isProductionMode()) {
                throw new IllegalStateException(
                        "Development profile is active but Vaadin is running in production mode. This indicates a configuration error - development profile should not be used in production.");
            }
            var routeConfiguration = RouteConfiguration.forApplicationScope();
            routeConfiguration.setRoute(DevLoginView.LOGIN_PATH, DevLoginView.class);
        };
    }
}
