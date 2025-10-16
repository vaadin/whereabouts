package com.example.application.humanresources.ui;

import com.example.application.common.address.PostalAddress;
import com.example.application.common.ui.AppIcon;
import com.example.application.common.ui.SectionToolbar;
import com.example.application.humanresources.*;
import com.example.application.security.AppRoles;
import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.signals.ValueSignal;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

@Route(value = "locations/:locationId", layout = LocationListView.class)
@RolesAllowed(AppRoles.LOCATION_READ)
class LocationDetailsView extends VerticalLayout implements AfterNavigationObserver, HasDynamicTitle {

    // TODO Make this view responsive
    // TODO Make this view accessible

    public static final String PARAM_LOCATION_ID = "locationId";
    private final LocationService locationService;

    private final ValueSignal<Location> locationSignal = new ValueSignal<>(Location.class);

    LocationDetailsView(AuthenticationContext authenticationContext, LocationService locationService) {
        this.locationService = locationService;
        var canCreate = authenticationContext.hasRole(AppRoles.LOCATION_CREATE);

        // Create components
        var title = new H2();
        var editButton = new Button("Edit", e -> edit());
        editButton.setVisible(canCreate);
        // TODO Delete button?
        var closeButton = new Button();
        closeButton.getElement().appendChild(AppIcon.CLOSE.create().getElement()); // Until we get an icon-only button variant for Aura
        closeButton.addClickListener(e -> HumanResourcesNavigation.navigateToLocationList());

        var about = new AboutSection();
        var summary = new SummarySection();
        var facilities = new FacilitiesSection();

        // Layout components
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        add(new SectionToolbar(title, SectionToolbar.group(editButton, closeButton)));

        var sections = new FormLayout(about, summary, facilities);
        sections.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("1000px", 2),
                new FormLayout.ResponsiveStep("1500px", 3)
        );
        add(new Scroller(sections));

        // Populate components
        var locationTypeFormatter = LocationTypeFormatter.ofLocale(getLocale());
        ComponentEffect.effect(this, () -> {
            var location = locationSignal.value();
            if (location != null) {
                title.setText("%s (%s) %s".formatted(location.data().name(), locationTypeFormatter.getDisplayName(location.data().locationType()), location.data().address().country().flagUnicode()));
                about.setAbout(location.data().about());
                summary.setEmployees(locationService.findLocationNodeById(location.id())
                        .map(LocationTreeNode.LocationNode::employees)
                        .orElse(0)); // TODO This is a bit quick and dirty
                summary.setEstablished(location.data().established());
                summary.setAddress(location.data().address());
                facilities.clear();
                location.data().facilities().forEach(facility -> {
                    switch (facility) {
                        case LocationFacility.AccessibleOffice ignored -> facilities.setAccessibleOffice();
                        case LocationFacility.FloorSpace floorSpace ->
                                facilities.setFloorSpace(floorSpace.squareMeters());
                        case LocationFacility.HotDesks hotDesks -> facilities.setHotDesks(hotDesks.number());
                        case LocationFacility.Kitchen ignored -> facilities.setKitchen();
                        case LocationFacility.MeetingBooths meetingBooths ->
                                facilities.setMeetingBooths(meetingBooths.number());
                        case LocationFacility.ParkingSlots parkingSlots ->
                                facilities.setParkingSlots(parkingSlots.number());
                    }
                });
                editButton.setEnabled(true);
            }
        });
    }

    private static class AboutSection extends VerticalLayout {

        private final Div text;

        AboutSection() {
            text = new Div();

            setSizeUndefined();
            add(new H4("About"));
            add(text);
        }

        public void setAbout(String about) {
            text.setText(about);
        }
    }

    private static class SummarySection extends VerticalLayout {

        private final DateTimeFormatter formatter;
        private final IconItem employees;
        private final IconItem established;
        private final IconItem address;

        SummarySection() {
            formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

            employees = new IconItem(AppIcon.DIVERSITY.create(), "Employees");
            established = new IconItem(AppIcon.CALENDAR_MONTH.create(), "Established");
            address = new IconItem(AppIcon.GLOBE_LOCATION_PIN.create(), "Address");

            setSizeUndefined();
            add(new H4("Summary"));
            add(employees, established, address);
        }

        public void setEmployees(int employees) {
            this.employees.setValue(Integer.toString(employees));
        }

        public void setEstablished(LocalDate established) {
            this.established.setValue(formatter.format(established));
        }

        public void setAddress(PostalAddress address) {
            this.address.setValue(address.toFormattedString());
        }
    }

    private static class FacilitiesSection extends VerticalLayout {

        private final IconItem floorSpace;
        private final IconItem hotDesks;
        private final IconItem kitchen;
        private final IconItem meetingBooths;
        private final IconItem accessibleOffice;
        private final IconItem parkingSlots;

        FacilitiesSection() {
            floorSpace = new IconItem(AppIcon.APARTMENT.create(), "Floor Space");
            hotDesks = new IconItem(AppIcon.DESK.create(), "Hot Desks");
            kitchen = new IconItem(AppIcon.FLATWARE.create(), "Kitchen");
            meetingBooths = new IconItem(AppIcon.MEETING_ROOM.create(), "Meeting Booths");
            accessibleOffice = new IconItem(AppIcon.ACCESSIBLE.create(), "Accessible Office");
            parkingSlots = new IconItem(AppIcon.PARKING_SIGN.create(), "Parking Slots");

            setSizeUndefined();
            add(new H4("Facilities"));
            add(accessibleOffice, floorSpace, hotDesks, kitchen, meetingBooths, parkingSlots);
        }

        public void clear() {
            floorSpace.setVisible(false);
            hotDesks.setVisible(false);
            kitchen.setVisible(false);
            meetingBooths.setVisible(false);
            accessibleOffice.setVisible(false);
            parkingSlots.setVisible(false);
        }

        public void setFloorSpace(int squareMeters) {
            floorSpace.setValue("%d m²".formatted(squareMeters));
            floorSpace.setVisible(true);
        }

        public void setHotDesks(int number) {
            hotDesks.setValue(Integer.toString(number));
            hotDesks.setVisible(true);
        }

        public void setKitchen() {
            kitchen.setVisible(true);
        }

        public void setMeetingBooths(int number) {
            meetingBooths.setValue(Integer.toString(number));
            meetingBooths.setVisible(true);
        }

        public void setAccessibleOffice() {
            accessibleOffice.setVisible(true);
        }

        public void setParkingSlots(int number) {
            parkingSlots.setValue(Integer.toString(number));
            parkingSlots.setVisible(true);
        }
    }

    private static class IconItem extends HorizontalLayout {

        private final Span valueSpan;

        public IconItem(AbstractIcon<?> icon, String label) {
            var labelDiv = new Div(label);
            labelDiv.setWidth(130, Unit.PIXELS);

            valueSpan = new Span();

            add(icon);
            add(labelDiv, valueSpan);
            setFlexShrink(0, labelDiv);
        }

        public void setValue(String value) {
            valueSpan.setText(value);
        }
    }

    private void edit() {
        var current = locationSignal.value();
        if (current != null) {
            var dialog = new EditLocationDialog(current, updated -> {
                var saved = locationService.update(updated);
                locationSignal.value(saved);
                getLocationListView().ifPresent(view -> view.onLocationUpdated(saved));
            });
            dialog.open();
        }
    }

    private Optional<LocationListView> getLocationListView() {
        return getParent().filter(LocationListView.class::isInstance).map(LocationListView.class::cast);
    }

    @Override
    public String getPageTitle() {
        return "Location Details - " + Optional.ofNullable(locationSignal.value()).map(location -> location.data().name()).orElse("");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        afterNavigationEvent.getRouteParameters()
                .getLong(PARAM_LOCATION_ID)
                .map(LocationId::of)
                .flatMap(locationService::findById)
                .ifPresentOrElse(locationSignal::value, HumanResourcesNavigation::navigateToLocationList);
    }
}
