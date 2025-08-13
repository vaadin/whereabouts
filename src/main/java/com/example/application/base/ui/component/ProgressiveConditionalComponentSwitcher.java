package com.example.application.base.ui.component;

import com.vaadin.flow.shared.Registration;

public class ProgressiveConditionalComponentSwitcher<T> {

    private ProgressiveConditionalComponentSwitcher() {
    }

    public static <T> Registration switchStateOnResize(ConditionalComponent<T> conditionalComponent, ResizeObserver resizeObserver, StateSupplier<T> stateSupplier) {
        return resizeObserver.addListener(event -> {
            conditionalComponent.setState(stateSupplier.getStateForSize(event.width(), event.height()));
        });
    }

    @FunctionalInterface
    public interface StateSupplier<T> {
        T getStateForSize(int width, int height);
    }
}
