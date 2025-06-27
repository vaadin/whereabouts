package com.example.application.base.ui.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import jakarta.annotation.security.PermitAll;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

@Layout
@PermitAll // When security is enabled, allow all authenticated users
public final class MainLayout extends AppLayout {

    MainLayout() {
        addClassName("main-layout");
        setPrimarySection(Section.DRAWER);
        addToDrawer(createHeader(), new Scroller(createSideNav()));
    }

    private Div createHeader() {
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.addClassNames("app-logo", IconSize.LARGE);

        var appName = new Span("CRUD Example");
        appName.addClassNames("app-name", FontWeight.SEMIBOLD, FontSize.LARGE);

        var header = new Div(appLogo, appName);
        header.addClassNames(Display.FLEX, Padding.MEDIUM, Gap.MEDIUM, AlignItems.CENTER);
        return header;
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(Margin.Horizontal.MEDIUM);
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new SvgIcon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

}
