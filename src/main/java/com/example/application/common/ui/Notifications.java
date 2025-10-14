package com.example.application.common.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

@NullMarked
public final class Notifications {

    private Notifications() {
    }

    public static Notification createNonCriticalNotification(Component prefixComponent, String text) {
        return createNotification(prefixComponent, text, Notification.Position.BOTTOM_END,
                Duration.ofSeconds(5));
    }

    public static Notification createCriticalNotification(Component prefixComponent, String text) {
        return createNotification(prefixComponent, text, Notification.Position.TOP_CENTER, null);
    }

    public static Notification createNotification(Component prefixComponent, String text, Notification.Position position, @Nullable Duration duration) {
        var notification = new Notification();
        notification.setPosition(position);
        notification.setDuration(duration == null ? 0 : (int) duration.toMillis());
        notification.setAssertive(true);

        var closeButton = new Button("Close", e -> notification.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        var textSpan = new Span(text);
        var content = new HorizontalLayout(prefixComponent, textSpan, closeButton);
        content.setSpacing(true);
        content.setPadding(true);
        content.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        content.setFlexGrow(1, textSpan);
        notification.add(content);

        return notification;
    }

    public static Notification createOptimisticLockingFailureNotification() {
        return Notifications.createCriticalNotification(AppIcon.PERSON_PLAY.create(AppIcon.Size.M, AppIcon.Color.ORANGE),
                "Another user has edited the information. Please refresh and try again.");
    }
}
