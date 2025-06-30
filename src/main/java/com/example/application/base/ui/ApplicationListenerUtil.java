package com.example.application.base.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.VaadinServletContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class ApplicationListenerUtil {
    private ApplicationListenerUtil() {
    }

    // TODO Can this be made to work so that you annotate a method on the component, and then Vaadin detects it and
    //  registers the listener, a bit like @ApplicationListener?

    public static <T> void handleEventsWhileAttached(Component component, SerializableConsumer<T> eventHandler) {
        component.addAttachListener(attachEvent -> {
            var ui = attachEvent.getUI();
            var servletContext = ((VaadinServletContext) ui.getSession().getService().getContext()).getContext();
            var applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            var eventMulticaster = applicationContext.getBean(ApplicationEventMulticaster.class);

            var listener = ApplicationListener.forPayload(attachEvent.getUI().accessLater(eventHandler, null));
            eventMulticaster.addApplicationListener(listener);
            component.addDetachListener(detachEvent -> {
                eventMulticaster.removeApplicationListener(listener);
                detachEvent.unregisterListener();
            });
        });
    }
}
