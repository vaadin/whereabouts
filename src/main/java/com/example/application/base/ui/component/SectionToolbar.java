package com.example.application.base.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;

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
}
