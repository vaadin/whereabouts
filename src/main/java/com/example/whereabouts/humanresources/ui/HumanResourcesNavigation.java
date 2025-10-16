package com.example.whereabouts.humanresources.ui;

import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.humanresources.LocationId;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParam;

public final class HumanResourcesNavigation {

    private HumanResourcesNavigation() {
    }

    public static void navigateToEmployeeDetails(EmployeeId id) {
        UI.getCurrent().navigate(EmployeeDetailsView.class, new RouteParam(EmployeeDetailsView.PARAM_EMPLOYEE_ID, id.toLong()));
    }

    public static void navigateToEmployeeList() {
        UI.getCurrent().navigate(EmployeeListView.class);
    }

    public static void navigateToLocationDetails(LocationId id) {
        UI.getCurrent().navigate(LocationDetailsView.class, new RouteParam(LocationDetailsView.PARAM_LOCATION_ID, id.toLong()));
    }

    public static void navigateToLocationList() {
        UI.getCurrent().navigate(LocationListView.class);
    }
}
