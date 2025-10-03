package com.example.application.humanresources.location.ui;

import com.example.application.AppRoles;
import com.example.application.base.ui.view.MainLayout;
import com.example.application.common.ui.AppIcon;
import com.example.application.common.ui.SectionToolbar;
import com.example.application.humanresources.location.Location;
import com.example.application.humanresources.location.LocationId;
import com.example.application.humanresources.location.LocationService;
import com.example.application.humanresources.location.LocationTreeNode;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

import java.util.stream.Stream;

@ParentLayout(MainLayout.class)
@Route(value = "locations", layout = MainLayout.class)
@PageTitle("Locations")
@Menu(order = 3, title = "Locations", icon = "icons/apartment.svg")
@PermitAll
class LocationListView extends MasterDetailLayout implements AfterNavigationObserver {

    private final LocationService locationService;
    private final LocationList locationList;

    LocationListView(AuthenticationContext authenticationContext, LocationService locationService) {
        this.locationService = locationService;
        var isAdmin = authenticationContext.hasRole(AppRoles.ADMIN);
        this.locationList = new LocationList(isAdmin);

        // Add listeners
        addBackdropClickListener(e -> locationList.grid.deselectAll());

        // Layout components
        setMaster(locationList);
        setOrientation(Orientation.VERTICAL);
        setDetailSize(300, Unit.PIXELS);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        afterNavigationEvent.getRouteParameters()
                .getLong(LocationDetailsView.PARAM_LOCATION_ID)
                .map(LocationId::of)
                .flatMap(locationService::getLocationNodeById)
                .ifPresentOrElse(locationList.grid::select, locationList.grid::deselectAll);
    }

    void onLocationUpdated(Location location) {
        // Maintain selection of the item after updating. Because we're using records and the grid uses object equality
        // to identify items, we have to do it like this. TODO Would be better to handle selection by ID rather than item
        var isSelected = locationList.grid
                .getSelectionModel()
                .getFirstSelectedItem()
                .filter(node -> node instanceof LocationTreeNode.LocationNode locationNode
                        && locationNode.id().equals(location.id()))
                .isPresent();
        locationList.grid.getDataProvider().refreshAll();
        if (isSelected) {
            locationService.getLocationNodeById(location.id()).ifPresent(locationList.grid::select);
        }
    }

    private class LocationList extends VerticalLayout {

        private final TreeGrid<LocationTreeNode> grid;

        LocationList(boolean isAdmin) {
            var title = new H2("Locations");

            var addLocationButton = new Button("Add Location");
            addLocationButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            addLocationButton.setVisible(isAdmin);

            var refreshButton = new Button(AppIcon.REFRESH.create());
            refreshButton.addThemeVariants(ButtonVariant.LUMO_ICON);

            grid = new TreeGrid<>();
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.setDataProvider(new AbstractBackEndHierarchicalDataProvider<>() {

                @Override
                public int getChildCount(HierarchicalQuery<LocationTreeNode, Object> query) {
                    return locationService.getChildCount(query.getParent());
                }

                @Override
                public boolean hasChildren(LocationTreeNode item) {
                    return locationService.hasChildren(item);
                }

                @Override
                protected Stream<LocationTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<LocationTreeNode, Object> query) {
                    return locationService.getChildren(query.getParent(), VaadinSpringDataHelpers.toSpringPageRequest(query)).stream();
                }
            });
            grid.addHierarchyColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode -> locationNode.name();
                case LocationTreeNode.CountryNode countryNode -> countryNode.country().displayName();
            }).setHeader("Branches").setSortProperty(LocationService.SORT_BY_LOCATION).setAutoWidth(true);
            grid.addColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode -> Integer.toString(locationNode.employees());
                case LocationTreeNode.CountryNode countryNode -> Integer.toString(countryNode.employees());
            }).setHeader("Employees").setSortProperty(LocationService.SORT_BY_EMPLOYEES).setAutoWidth(true);
            grid.addColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode -> locationNode.locationType().displayName();
                case LocationTreeNode.CountryNode ignored -> "";
            }).setHeader("Type").setSortProperty(LocationService.SORT_BY_TYPE).setAutoWidth(true);
            grid.addColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode -> locationNode.address().toFormattedString();
                case LocationTreeNode.CountryNode ignored -> "";
            }).setHeader("Address").setFlexGrow(1);

            // Add listeners
            grid.addSelectionListener(e -> e.getFirstSelectedItem()
                    .ifPresentOrElse(
                            node -> {
                                if (node instanceof LocationTreeNode.LocationNode locationNode) {
                                    LocationNavigation.navigateToLocationDetails(locationNode.id());
                                }
                            },
                            LocationNavigation::navigateToLocationList
                    ));
            addLocationButton.addClickListener(e -> addLocation());
            refreshButton.addClickListener(e -> grid.getDataProvider().refreshAll());

            // Layout components
            var toolbar = new SectionToolbar(
                    SectionToolbar.group(new DrawerToggle(), title),
                    SectionToolbar.group(addLocationButton, refreshButton)
            );
            setSizeFull();
            add(toolbar, grid);
            getFlexGrow(grid);
        }

        private void addLocation() {
            var dialog = new AddLocationDialog(locationData -> {
                var id = locationService.insert(locationData);
                grid.getDataProvider().refreshAll();
                LocationNavigation.navigateToLocationDetails(id);
            });
            dialog.open();
        }
    }
}
