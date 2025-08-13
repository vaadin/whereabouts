package com.example.application.base.ui.component;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResizeObserver implements Serializable {

    private final List<SerializableConsumer<ResizeEvent>> listeners = new ArrayList<>();

    private final HasElement component;
    private int width = -1;
    private int height = -1;

    public ResizeObserver(HasElement component) {
        this.component = component;
        component.getElement().addAttachListener(event -> {
            // TODO Need a unique name instead of vaadinFlowResizeObserver
            event.getSource().executeJs("""
                    const resizeObserver = new ResizeObserver((entries) => {
                        const rect = entries.at(0).contentRect;
                        const w = rect.width;
                        const h = rect.height;
                        const event = new CustomEvent("content-resize", { detail: { w: w, h: h}});
                        $0.dispatchEvent(event);
                    });
                    resizeObserver.observe($0);
                    $0.vaadinFlowResizeObserver = resizeObserver;
                    """, event.getSource());
        });
        component.getElement().addDetachListener(event -> {
            event.getSource().executeJs("""
                    const resizeObserver = $0.vaadinFlowResizeObserver;
                    if (resizeObserver) {
                        resizeObserver.disconnect();
                        delete $0.vaadinFlowResizeObserver;
                    }
                    """, event.getSource());
        });
        var listenerRegistration = component.getElement().addEventListener("content-resize", event -> {
            this.width = (int) event.getEventData().getNumber("event.detail.w");
            this.height = (int) event.getEventData().getNumber("event.detail.h");
            fireEvent(new ResizeEvent(component, width, height));
        });
        listenerRegistration.addEventData("event.detail.w");
        listenerRegistration.addEventData("event.detail.h");
        listenerRegistration.allowInert();
        listenerRegistration.debounce(80); // TODO Tweak this
    }

    private void fireEvent(ResizeEvent resizeEvent) {
        List.copyOf(listeners).forEach(listener -> listener.accept(resizeEvent));
    }

    public Registration addListener(SerializableConsumer<ResizeEvent> listener) {
        listeners.add(listener);
        listener.accept(new ResizeEvent(this.component, width, height));
        return () -> listeners.remove(listener);
    }

    public record ResizeEvent(HasElement source, int width, int height) {
    }
}
