package com.example.whereabouts.common.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SectionToolbar extends Composite<VerticalLayout> {

    public SectionToolbar(Component... components) {
        var layout = getContent();
        layout.setPadding(true);
        addRow(components);
    }

    public void setPadding(boolean padding) {
        getContent().setPadding(padding);
    }

    public void addRow(Component... components) {
        var row = new HorizontalLayout(components);
        row.setWrap(true);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        getContent().add(row);
    }

    public SectionToolbar withRow(Component... components) {
        addRow(components);
        return this;
    }

    public static Component group(Component... components) {
        var group = new HorizontalLayout(components);
        group.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        group.setWrap(true);
        return group;
    }
}
