package com.example.application.base.ui.component;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.spring.security.AuthenticationContext;

public final class ViewHeader extends Composite<Header> {

    public ViewHeader(AuthenticationContext authenticationContext, String viewTitle) {
        addClassNames("view-header");

        var drawerToggle = new DrawerToggle();

        var title = new H1(viewTitle);

        var toggleAndTitle = new Div(drawerToggle, title);
        toggleAndTitle.addClassNames("view-header-title-container");
        getContent().add(toggleAndTitle);

        getContent().add(new UserMenu(authenticationContext));
    }
}
