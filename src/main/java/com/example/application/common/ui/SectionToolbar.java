package com.example.application.common.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import org.jspecify.annotations.NullMarked;

@NullMarked
@CssImport("./section-toolbar.css")
public final class SectionToolbar extends Composite<Header> {

    public SectionToolbar(Component... components) {
        addClassNames("section-toolbar");
        addRow(components);
    }

    public void addRow(Component... components) {
        var row = new Div(components);
        row.addClassNames("section-toolbar-row");
        getContent().add(row);
    }

    public SectionToolbar withRow(Component... components) {
        addRow(components);
        return this;
    }

    public static Component group(Component... components) {
        var group = new Div(components);
        group.addClassNames("section-toolbar-group");
        return group;
    }
}
