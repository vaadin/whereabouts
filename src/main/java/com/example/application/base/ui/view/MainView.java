package com.example.application.base.ui.view;

import com.example.application.base.ui.component.ViewHeader;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

/**
 * This view shows up when a user navigates to the root ('/') of the application.
 */
@Route
@PermitAll // When security is enabled, allow all authenticated users
public final class MainView extends Main {

    // TODO Replace with your own main view.

    MainView(AuthenticationContext authenticationContext) {
        addClassName(LumoUtility.Padding.MEDIUM);
        add(new ViewHeader(authenticationContext, "Main"));
        add(new Div("Please select a view from the menu on the left."));
    }

    /**
     * Navigates to the main view.
     */
    public static void showMainView() {
        UI.getCurrent().navigate(MainView.class);
    }
}
