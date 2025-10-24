package com.example.whereabouts.projects.ui;

import com.example.whereabouts.projects.ProjectId;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParam;

/**
 * @see "Design decision: DD007-20251024-navigation-patterns.md"
 */
public final class ProjectsNavigation {

    private ProjectsNavigation() {
    }

    public static void navigateToProjectList() {
        UI.getCurrent().navigate(ProjectListView.class);
    }

    public static void navigateToProjectDetails(ProjectId id) {
        UI.getCurrent().navigate(ProjectDetailsView.class, new RouteParam(ProjectDetailsView.PARAM_PROJECT_ID, id.toLong()));
    }
}
