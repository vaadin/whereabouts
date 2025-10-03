package com.example.application.common.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

@NullMarked
public final class Notifications {

    private Notifications() {
    }

    public static Notification createNonCriticalNotification(Component prefixComponent, String text,
                                                             NotificationVariant variant) {
        return createNotification(prefixComponent, text, variant, Notification.Position.BOTTOM_END,
                Duration.ofSeconds(5));
    }

    public static Notification createCriticalNotification(Component prefixComponent, String text,
                                                          NotificationVariant variant) {
        return createNotification(prefixComponent, text, variant, Notification.Position.MIDDLE, null);
    }

    public static Notification createNotification(Component prefixComponent, String text, NotificationVariant variant,
                                                  Notification.Position position, @Nullable Duration duration) {
        var notification = new Notification();
        notification.setPosition(position);
        notification.setDuration(duration == null ? 0 : (int) duration.toMillis());
        notification.addThemeVariants(variant);
        notification.setAssertive(true);

        var closeButton = new Button("Close", e -> notification.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        var content = new HorizontalLayout(prefixComponent, new Span(text), closeButton);
        content.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        notification.add(content);

        return notification;
    }
}
