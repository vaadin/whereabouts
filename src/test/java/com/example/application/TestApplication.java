package com.example.application;

import com.vaadin.flow.server.Constants;
import com.vaadin.flow.server.frontend.FileIOUtils;
import com.vaadin.flow.server.frontend.FrontendUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Run this application class to start your application locally, using Testcontainers for all external services. You
 * have to configure the containers in {@link TestcontainersConfiguration}.
 */
public class TestApplication {

    public static void main(String[] args) {
        initializeProjectFolder();
        SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
    }

    /**
     * Vaadin does not recognize starting the application from {@code test-classes} and deduces the wrong project
     * directory. This method is a workaround that deduces the project folder correctly and uses a system property to
     * instruct Vaadin to use it.
     */
    private static void initializeProjectFolder() {
        try {
            System.setProperty(Constants.VAADIN_PREFIX + FrontendUtils.PROJECT_BASEDIR,
                    getProjectFolder().getAbsolutePath());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize project folder", e);
        }
    }

    private static @NonNull File getProjectFolder() throws URISyntaxException {
        URL url = FileIOUtils.class.getClassLoader().getResource(".");
        if (url != null && url.getProtocol().equals("file")) {
            Path path = Path.of(url.toURI());
            if (path.endsWith(Path.of("target", "test-classes")) || path.endsWith(Path.of("target", "classes"))) {
                return path.getParent().getParent().toFile();
            }
        }
        throw new IllegalStateException("Could not find project folder");
    }
}
