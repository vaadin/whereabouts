package com.example.whereabouts.common.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@NullMarked
class MainErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(MainErrorHandler.class);

    @Bean
    public VaadinServiceInitListener errorHandlerInitializer() {
        return (event) -> event.getSource().addSessionInitListener(
                sessionInitEvent -> sessionInitEvent.getSession().setErrorHandler(errorEvent -> {
                    log.error("An unexpected error occurred", errorEvent.getThrowable());
                    errorEvent.getComponent().flatMap(Component::getUI).ifPresent(ui -> {
                        var notification = Notifications.createCriticalNotification(AppIcon.ERROR.create(AppIcon.Size.L, AppIcon.Color.RED),
                                "An unexpected error has occurred. Please try again later.");
                        ui.access(notification::open);
                    });
                }));
    }
}
