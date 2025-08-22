package com.example.application.base.ui.component;

import com.vaadin.flow.component.html.Span;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Badges {

    private Badges() {
    }

    public static Span createContrast(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge contrast");
        return badge;
    }

    public static Span createSuccess(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge success");
        return badge;
    }

    public static Span createError(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge error");
        return badge;
    }

    public static Span createWarning(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge warning");
        return badge;
    }

    public static Span createDefault(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge");
        return badge;
    }
}
