package com.example.application.base.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public final class Notifications {

    private Notifications() {
    }

    public static Notification createNotification(Component prefixComponent, String text, NotificationVariant variant) {
        var notification = new Notification();
        notification.setPosition(Notification.Position.BOTTOM_END);
        notification.setDuration(5000);
        notification.addThemeVariants(variant);
        notification.add(new HorizontalLayout(prefixComponent, new Text(text)));
        return notification;
    }
}
