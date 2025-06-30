package com.example.application.base.ui.component;

import com.example.application.security.AppUserPrincipal;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class UserMenu extends Composite<MenuBar> {

    public UserMenu(AuthenticationContext authenticationContext) {
        var user = authenticationContext.getAuthenticatedUser(AppUserPrincipal.class).orElseThrow().getAppUser();

        var avatar = new Avatar(user.getFullName(), user.getPictureUrl());
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.addClassNames(LumoUtility.Margin.Right.SMALL);
        // Make the avatar a little nicer looking if there is no picture URL
        avatar.setColorIndex(user.getPreferredUsername().hashCode() % 7);

        var userMenu = getContent();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        var userMenuItem = userMenu.addItem(avatar);
        userMenuItem.add(user.getFullName());
        if (user.getProfileUrl() != null) {
            userMenuItem.getSubMenu().addItem("View Profile",
                    event -> UI.getCurrent().getPage().open(user.getProfileUrl()));
        }
        userMenuItem.getSubMenu().addItem("Logout", event -> authenticationContext.logout());
        addClassNames(LumoUtility.Margin.Horizontal.MEDIUM);
    }
}
