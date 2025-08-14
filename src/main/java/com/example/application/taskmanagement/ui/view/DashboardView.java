package com.example.application.taskmanagement.ui.view;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("")
@PageTitle("Dashboard")
@Menu(icon = "icons/speed.svg", title = "Dashboard")
@PermitAll
public class DashboardView extends Main {
    // TODO Implement DashboardView
}
