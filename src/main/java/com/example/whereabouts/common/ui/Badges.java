package com.example.whereabouts.common.ui;

import com.vaadin.flow.component.html.Span;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Badges {

    private Badges() {
    }

    public static Span create(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge");
        return badge;
    }

    public static Span createGreen(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge green");
        return badge;
    }

    public static Span createRed(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge red");
        return badge;
    }

    public static Span createYellow(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge yellow");
        return badge;
    }

    public static Span createBlue(String text) {
        var badge = new Span(text);
        badge.getElement().getThemeList().add("badge blue");
        return badge;
    }
}
