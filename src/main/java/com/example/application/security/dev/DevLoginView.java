package com.example.application.security.dev;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Login view for development.
 */
@PageTitle("Login")
@AnonymousAllowed
@CssImport("./themes/default/dev-login.css")
// No @Route annotation - the route is registered dynamically by DevSecurityConfig.
class DevLoginView extends Main implements BeforeEnterObserver {

    static final String LOGIN_PATH = "dev-login";

    private final AuthenticationContext authenticationContext;
    private final LoginForm login;

    DevLoginView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

        // Create the components
        login = new LoginForm();
        login.setAction(LOGIN_PATH);

        var userList = new UnorderedList();
        SampleUsers.ALL_USERS.forEach(user -> userList.add(new ListItem(user.getAppUser().getPreferredUsername())));

        // Configure the view
        setSizeFull();
        var exampleUsers = new Div(new H3("Example users"),
                new Paragraph("The password for every user is: " + SampleUsers.SAMPLE_PASSWORD), userList);
        var centerDiv = new Div(login, exampleUsers);
        add(centerDiv);

        var devModeMenuDiv = new Div("You can also use the Dev Mode Menu here to impersonate any user!");
        devModeMenuDiv.addClassNames("dev-mode-speech-bubble");
        add(devModeMenuDiv);

        // Style the view
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.JustifyContent.CENTER, LumoUtility.AlignItems.CENTER,
                LumoUtility.Background.CONTRAST_5);
        centerDiv.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Gap.SMALL);
        exampleUsers.addClassNames(LumoUtility.Background.BASE, LumoUtility.Padding.LARGE);
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
