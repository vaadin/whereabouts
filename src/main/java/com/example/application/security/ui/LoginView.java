package com.example.application.security.ui;

import com.example.application.common.ui.AppIcon;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.jspecify.annotations.NullMarked;

@PageTitle("Login")
@Route(value = "login", autoLayout = false)
@AnonymousAllowed
@NullMarked
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticationContext authenticationContext;
    private final LoginForm login;

    LoginView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

        // Create the components
        login = new LoginForm();
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        var appLogo = AppIcon.ENGINEERING.create(AppIcon.Size.XL, AppIcon.Color.PURPLE);

        var appName = new Span("Whereabouts");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);

        // Configure the view
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        var centerLayout = new VerticalLayout(appLogo, appName, login);
        centerLayout.setAlignItems(Alignment.CENTER);
        centerLayout.setSizeUndefined();
        centerLayout.getStyle().setBorder("1px solid var(--vaadin-border-color)");
        centerLayout.getStyle().setBackground("var(--vaadin-background-color)");
        centerLayout.getStyle().setBorderRadius("var(--vaadin-radius-m)");

        add(centerLayout);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticationContext.isAuthenticated()) {
            // Redirect to the main view if the user is already logged in. This makes impersonation easier to work with.
            event.forwardTo("");
            return;
        }

        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}
