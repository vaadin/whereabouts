package com.example.application.base.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;

import java.util.Map;
import java.util.Objects;

public class ConditionalComponent<T> extends Component {

    private final Map<T, Component> components;
    private T state;

    public ConditionalComponent(Map<T, Component> components, T initialState) {
        super(null);
        if (components.isEmpty()) {
            throw new IllegalArgumentException("Components must not be empty");
        }
        this.components = Map.copyOf(components);
        this.state = initialState;
    }

    public T getState() {
        return state;
    }

    public void setState(T state) {
        if (Objects.equals(this.state, state)) {
            return;
        }

        if (!components.containsKey(state)) {
            throw new IllegalArgumentException("State must exist");
        }

        var oldState = this.state;
        this.state = state;

        var oldElement = components.get(oldState).getElement();
        var parent = oldElement.getParent();
        if (parent != null) {
            var index = parent.indexOfChild(oldElement);
            oldElement.removeFromParent();
            var newElement = components.get(state).getElement();
            parent.insertChild(index, newElement);
        }
    }

    @Override
    public Element getElement() {
        return components.get(state).getElement();
    }

    public static ConditionalComponent<Boolean> createBinary(Component trueComponent, Component falseComponent, boolean initialState) {
        return new ConditionalComponent<>(Map.of(true, trueComponent, false, falseComponent), initialState);
    }

    public static <T> ConditionalComponent<T> createBistate(T s1, Component c1, T s2, Component c2, T initialState) {
        return new ConditionalComponent<>(Map.of(s1, c1, s2, c2), initialState);
    }

    public static ConditionalComponent<Component> createBistate(Component c1, Component c2, Component initialState) {
        return new ConditionalComponent<>(Map.of(c1, c1, c2, c2), initialState);
    }

    public static <T> ConditionalComponent<T> createTristate(T s1, Component c1, T s2, Component c2, T s3, Component c3, T initialState) {
        return new ConditionalComponent<>(Map.of(s1, c1, s2, c2, s3, c3), initialState);
    }
}
