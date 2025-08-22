package com.example.application.base.ui.view;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
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
public class LoginView extends Main implements BeforeEnterObserver {

    private final AuthenticationContext authenticationContext;
    private final LoginForm login;

    LoginView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

        // Create the components
        login = new LoginForm();
        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        // Configure the view
        setSizeFull();
        addClassNames("login-view");

        add(login);
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
