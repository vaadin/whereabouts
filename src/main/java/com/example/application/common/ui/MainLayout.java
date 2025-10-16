package com.example.application.common.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.jspecify.annotations.NullMarked;

@Layout
@PermitAll
@NullMarked
public final class MainLayout extends AppLayout {

    MainLayout(AuthenticationContext authenticationContext) {
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()), createUserMenu(authenticationContext));
    }

    private Component createHeader() {
        var appLogo = AppIcon.ENGINEERING.create(AppIcon.Size.XL, AppIcon.Color.PURPLE);

        var appName = new Span("Whereabouts");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);

        var header = new VerticalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        var item = new SideNavItem(menuEntry.title(), menuEntry.path());
        item.setMatchNested(true);

        if (menuEntry.icon() != null) {
            item.setPrefixComponent(new SvgIcon(menuEntry.icon()));
        }

        return item;
    }

    private Component createUserMenu(AuthenticationContext authenticationContext) {
        return new UserMenu(authenticationContext);
    }
}
