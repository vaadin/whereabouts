package com.example.application.base.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.jspecify.annotations.Nullable;

public class EmptyStateWrapper extends Composite<Div> implements HasSize {

    private @Nullable Component emptyComponent;
    private @Nullable Component nonEmptyComponent;
    private boolean empty;

    public EmptyStateWrapper() {
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
    }

    public EmptyStateWrapper(@Nullable Component emptyComponent, @Nullable Component nonEmptyComponent) {
        this();
        this.emptyComponent = emptyComponent;
        this.nonEmptyComponent = nonEmptyComponent;
        update();
    }

    public void setEmptyComponent(@Nullable Component emptyComponent) {
        this.emptyComponent = emptyComponent;
        update();
    }

    public @Nullable Component getEmptyComponent() {
        return emptyComponent;
    }

    public void setNonEmptyComponent(@Nullable Component nonEmptyComponent) {
        this.nonEmptyComponent = nonEmptyComponent;
        update();
    }

    public @Nullable Component getNonEmptyComponent() {
        return nonEmptyComponent;
    }

    public void setEmpty(boolean empty) {
        if (this.empty != empty) {
            this.empty = empty;
            update();
        }
    }

    public boolean isEmpty() {
        return empty;
    }

    private void update() {
        if (empty) {
            if (nonEmptyComponent != null) {
                nonEmptyComponent.removeFromParent();
            }
            if (emptyComponent != null) {
                getElement().appendChild(emptyComponent.getElement());
            }
        } else {
            if (emptyComponent != null) {
                emptyComponent.removeFromParent();
            }
            if (nonEmptyComponent != null) {
                getElement().appendChild(nonEmptyComponent.getElement());
            }
        }
    }
}
