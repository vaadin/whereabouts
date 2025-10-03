package com.example.application.humanresources.location.ui;

import com.example.application.humanresources.location.LocationId;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParam;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LocationNavigation {

    private LocationNavigation() {
    }

    public static void navigateToLocationDetails(LocationId id) {
        UI.getCurrent().navigate(LocationDetailsView.class, new RouteParam(LocationDetailsView.PARAM_LOCATION_ID, id.toLong()));
    }

    public static void navigateToLocationList() {
        UI.getCurrent().navigate(LocationListView.class);
    }
}
