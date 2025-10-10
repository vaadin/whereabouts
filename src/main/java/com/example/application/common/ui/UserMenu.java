package com.example.application.common.ui;

import com.example.application.security.User;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UserMenu extends Composite<MenuBar> {

    public UserMenu(AuthenticationContext authenticationContext) {
        var user = authenticationContext.getAuthenticatedUser(User.class).orElseThrow();

        var userMenu = getContent();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        var userMenuItem = userMenu.addItem(user.displayName());
        userMenuItem.getSubMenu().addItem("Logout", event -> authenticationContext.logout());
        addClassNames(LumoUtility.Margin.Horizontal.MEDIUM);
    }
}
