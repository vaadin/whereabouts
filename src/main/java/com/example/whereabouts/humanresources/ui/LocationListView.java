package com.example.whereabouts.humanresources.ui;

import com.example.whereabouts.MainLayout;
import com.example.whereabouts.common.ui.AppIcon;
import com.example.whereabouts.common.ui.SectionToolbar;
import com.example.whereabouts.humanresources.*;
import com.example.whereabouts.humanresources.Location;
import com.example.whereabouts.security.AppRoles;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;

import java.util.stream.Stream;

/**
 * @see "Design decision: DD008-20251024-master-detail.md"
 */
@ParentLayout(MainLayout.class)
@Route(value = "locations", layout = MainLayout.class)
@PageTitle("Locations")
@Menu(order = 3, title = "Locations", icon = "icons/apartment.svg")
@RolesAllowed(AppRoles.LOCATION_READ)
class LocationListView extends MasterDetailLayout implements AfterNavigationObserver {

    private final LocationService locationService;
    private final LocationList locationList;

    LocationListView(AuthenticationContext authenticationContext, LocationService locationService) {
        this.locationService = locationService;
        var canCreate = authenticationContext.hasRole(AppRoles.LOCATION_CREATE);
        this.locationList = new LocationList(canCreate);

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
                .flatMap(locationService::findLocationNodeById)
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
            locationService.findLocationNodeById(location.id()).ifPresent(locationList.grid::select);
        }
    }

    private class LocationList extends VerticalLayout {

        private final TreeGrid<LocationTreeNode> grid;

        LocationList(boolean canCreate) {
            var title = new H1("Locations");

            var addLocationButton = new Button("Add Location");
            addLocationButton.setVisible(canCreate);

            var refreshButton = new Button();
            refreshButton.getElement().appendChild(AppIcon.REFRESH.create().getElement()); // Until we get an icon-only button variant for Aura

            grid = new TreeGrid<>();
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.setDataProvider(new AbstractBackEndHierarchicalDataProvider<>() {

                @Override
                public int getChildCount(HierarchicalQuery<LocationTreeNode, Object> query) {
                    return locationService.countChildren(query.getParent());
                }

                @Override
                public boolean hasChildren(LocationTreeNode item) {
                    return locationService.hasChildren(item);
                }

                @Override
                protected Stream<LocationTreeNode> fetchChildrenFromBackEnd(HierarchicalQuery<LocationTreeNode, Object> query) {
                    return locationService.findChildren(query.getParent(), VaadinSpringDataHelpers.toSpringPageRequest(query)).stream();
                }
            });
            var locationTypeFormatter = LocationTypeFormatter.ofLocale(getLocale());
            grid.addHierarchyColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode -> locationNode.name();
                case LocationTreeNode.CountryNode countryNode ->
                        countryNode.country().displayName() + " " + countryNode.country().flagUnicode();
            }).setHeader("Branches").setSortProperty(LocationSortableProperty.NAME.name()).setAutoWidth(true);
            grid.addColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode -> Integer.toString(locationNode.employees());
                case LocationTreeNode.CountryNode countryNode -> Integer.toString(countryNode.employees());
            }).setHeader("Employees").setSortProperty(LocationSortableProperty.EMPLOYEES.name()).setAutoWidth(true);
            grid.addColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode ->
                        locationTypeFormatter.getDisplayName(locationNode.locationType());
                case LocationTreeNode.CountryNode ignored -> "";
            }).setHeader("Type").setSortProperty(LocationSortableProperty.LOCATION_TYPE.name()).setAutoWidth(true);
            grid.addColumn(node -> switch (node) {
                case LocationTreeNode.LocationNode locationNode -> locationNode.address().toFormattedString();
                case LocationTreeNode.CountryNode ignored -> "";
            }).setHeader("Address").setFlexGrow(1);
            grid.addThemeName("no-border");

            // Add listeners
            grid.addSelectionListener(e -> e.getFirstSelectedItem()
                    .ifPresentOrElse(
                            node -> {
                                if (node instanceof LocationTreeNode.LocationNode locationNode) {
                                    HumanResourcesNavigation.navigateToLocationDetails(locationNode.id());
                                }
                            },
                            HumanResourcesNavigation::navigateToLocationList
                    ));
            addLocationButton.addClickListener(e -> addLocation());
            refreshButton.addClickListener(e -> grid.getDataProvider().refreshAll());

            // Layout components
            var toolbar = new SectionToolbar(
                    SectionToolbar.group(new DrawerToggle(), title),
                    SectionToolbar.group(addLocationButton, refreshButton)
            );
            toolbar.getStyle().setBorderBottom("1px solid var(--vaadin-border-color-secondary)");
            setSizeFull();
            setPadding(false);
            setSpacing(false);
            getStyle().setOverflow(Style.Overflow.HIDDEN);

            add(toolbar, grid);
        }

        private void addLocation() {
            var dialog = new AddLocationDialog(locationData -> {
                var id = locationService.insert(locationData);
                grid.getDataProvider().refreshAll();
                HumanResourcesNavigation.navigateToLocationDetails(id);
            });
            dialog.open();
        }
    }
}
