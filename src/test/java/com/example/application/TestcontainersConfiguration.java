package com.example.application;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    // TODO Configure your Testcontainers here.
    //  See https://docs.spring.io/spring-boot/reference/testing/testcontainers.html for details.

    @Bean
    @ServiceConnection
    public JdbcDatabaseContainer<?> postgresqlContainer() {
        return new PostgreSQLContainer<>("postgres:17-alpine");
    }
}
