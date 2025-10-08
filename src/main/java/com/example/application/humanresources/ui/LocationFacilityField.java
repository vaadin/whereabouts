package com.example.application.humanresources.ui;

import com.example.application.humanresources.LocationFacility;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NullMarked
final class LocationFacilityField extends CustomField<List<LocationFacility>> {

    private final AccessibleOfficeField accessibleOfficeField = new AccessibleOfficeField();
    private final FloorSpaceField floorSpaceField = new FloorSpaceField();
    private final HotDesksField hotDeskField = new HotDesksField();
    private final KitchenField kitchenField = new KitchenField();
    private final MeetingBoothsField meetingBoothsField = new MeetingBoothsField();
    private final ParkingSlotsField parkingSlotsField = new ParkingSlotsField();


    LocationFacilityField() {
        var layout = new FormLayout();
        layout.add(accessibleOfficeField);
        layout.add(floorSpaceField);
        layout.add(hotDeskField);
        layout.add(kitchenField);
        layout.add(meetingBoothsField);
        layout.add(parkingSlotsField);
        add(layout);
    }

    private static abstract class FacilityField extends HorizontalLayout {

        private final Checkbox included;

        FacilityField(String label) {
            included = new Checkbox(label, e -> onIncludedChange(e.getValue()));
            setDefaultVerticalComponentAlignment(Alignment.CENTER);
            add(included);
        }

        protected void onIncludedChange(boolean included) {
            // NOP
        }

        public boolean isIncluded() {
            return included.getValue();
        }

        public void include() {
            included.setValue(true);
        }

        public void exclude() {
            included.setValue(false);
        }
    }

    private static abstract class QuantityFacilityField extends FacilityField {

        private final IntegerField quantity;

        QuantityFacilityField(String label, String quantityPlaceholder) {
            super(label);
            quantity = new IntegerField();
            quantity.setPlaceholder(quantityPlaceholder);
            quantity.setEnabled(false);
            quantity.setWidth(5, Unit.EM);
            add(quantity);
        }

        @Override
        protected void onIncludedChange(boolean included) {
            quantity.setEnabled(included);
        }

        public void include(int quantity) {
            super.include();
            this.quantity.setValue(quantity);
        }

        @Override
        public void exclude() {
            super.exclude();
            this.quantity.clear();
        }

        public int getQuantity() {
            return quantity.isEmpty() ? 0 : quantity.getValue();
        }

        @Override
        public boolean isIncluded() {
            return super.isIncluded() && getQuantity() > 0;
        }
    }

    private static class HotDesksField extends QuantityFacilityField {

        HotDesksField() {
            super("Hot desks", "Qty");
        }

        public Optional<LocationFacility.HotDesks> toHotDesks() {
            return isIncluded() ? Optional.of(new LocationFacility.HotDesks(getQuantity())) : Optional.empty();
        }
    }

    private static class KitchenField extends FacilityField {

        KitchenField() {
            super("Kitchen");
        }

        public Optional<LocationFacility.Kitchen> toKitchen() {
            return isIncluded() ? Optional.of(new LocationFacility.Kitchen()) : Optional.empty();
        }
    }

    private static class MeetingBoothsField extends QuantityFacilityField {

        MeetingBoothsField() {
            super("Meeting booths", "Qty");
        }

        public Optional<LocationFacility.MeetingBooths> toMeetingBooths() {
            return isIncluded() ? Optional.of(new LocationFacility.MeetingBooths(getQuantity())) : Optional.empty();
        }
    }

    private static class AccessibleOfficeField extends FacilityField {

        AccessibleOfficeField() {
            super("Accessible office");
        }

        public Optional<LocationFacility.AccessibleOffice> toAccessibleOffice() {
            return isIncluded() ? Optional.of(new LocationFacility.AccessibleOffice()) : Optional.empty();
        }
    }

    private static class ParkingSlotsField extends QuantityFacilityField {

        ParkingSlotsField() {
            super("Parking slots", "Qty");
        }

        public Optional<LocationFacility.ParkingSlots> toParkingSlots() {
            return isIncluded() ? Optional.of(new LocationFacility.ParkingSlots(getQuantity())) : Optional.empty();
        }
    }

    private static class FloorSpaceField extends QuantityFacilityField {

        FloorSpaceField() {
            super("Floor space", "m²");
        }

        public Optional<LocationFacility.FloorSpace> toFloorSpace() {
            return isIncluded() ? Optional.of(new LocationFacility.FloorSpace(getQuantity())) : Optional.empty();
        }
    }

    @Override
    protected List<LocationFacility> generateModelValue() {
        var facilities = new ArrayList<LocationFacility>();
        accessibleOfficeField.toAccessibleOffice().ifPresent(facilities::add);
        floorSpaceField.toFloorSpace().ifPresent(facilities::add);
        hotDeskField.toHotDesks().ifPresent(facilities::add);
        kitchenField.toKitchen().ifPresent(facilities::add);
        meetingBoothsField.toMeetingBooths().ifPresent(facilities::add);
        parkingSlotsField.toParkingSlots().ifPresent(facilities::add);
        return facilities;
    }

    @Override
    protected void setPresentationValue(List<LocationFacility> locationFacilities) {
        accessibleOfficeField.exclude();
        floorSpaceField.exclude();
        hotDeskField.exclude();
        kitchenField.exclude();
        meetingBoothsField.exclude();
        parkingSlotsField.exclude();
        locationFacilities.forEach(facility -> {
            switch (facility) {
                case LocationFacility.AccessibleOffice ignored -> accessibleOfficeField.include();
                case LocationFacility.FloorSpace floorSpace -> floorSpaceField.include(floorSpace.squareMeters());
                case LocationFacility.HotDesks hotDesks -> hotDeskField.include(hotDesks.number());
                case LocationFacility.Kitchen ignored -> kitchenField.include();
                case LocationFacility.MeetingBooths meetingBooths -> meetingBoothsField.include(meetingBooths.number());
                case LocationFacility.ParkingSlots parkingSlots -> parkingSlotsField.include(parkingSlots.number());
            }
        });
    }
}
