package com.example.whereabouts.common.ui;

import com.example.whereabouts.security.UserPrincipal;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UserMenu extends Composite<MenuBar> {

    public UserMenu(AuthenticationContext authenticationContext) {
        var user = authenticationContext.getAuthenticatedUser(UserPrincipal.class).orElseThrow();

        var userMenu = getContent();
        var userMenuItem = userMenu.addItem(user.getDisplayName());
        userMenuItem.getSubMenu().addItem("Logout", event -> authenticationContext.logout());
    }
}
