package com.example.application.base.ui.component;

import com.example.application.base.domain.UserPrincipal;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class UserMenu extends Composite<MenuBar> {

    public UserMenu(AuthenticationContext authenticationContext) {
        addClassName("user-menu");
        var user = authenticationContext.getAuthenticatedUser(UserPrincipal.class).orElseThrow().getUser();

        var avatar = new Avatar(user.getDisplayName());
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.addClassNames(LumoUtility.Margin.Right.SMALL);
        // Make the avatar a little nicer looking if there is no picture URL
        avatar.setColorIndex(user.getUsername().hashCode() % 7);

        var userMenu = getContent();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        var userMenuItem = userMenu.addItem(avatar);
        userMenuItem.add(user.getDisplayName());
        userMenuItem.getSubMenu().addItem("Logout", event -> authenticationContext.logout());
        addClassNames(LumoUtility.Margin.Horizontal.MEDIUM);
    }
}
