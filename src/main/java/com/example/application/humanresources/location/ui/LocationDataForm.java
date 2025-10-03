package com.example.application.humanresources.location.ui;

import com.example.application.common.ui.PostalAddressField;
import com.example.application.common.ui.TimeZoneField;
import com.example.application.humanresources.location.LocationData;
import com.example.application.humanresources.location.LocationType;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
class LocationDataForm extends Composite<FormLayout> {

    private final Binder<LocationData> binder;

    LocationDataForm() {
        // Create the components
        var nameField = new TextField();
        nameField.setLabel("Name");

        var locationTypeField = new Select<LocationType>();
        locationTypeField.setLabel("Type");
        locationTypeField.setItems(LocationType.values());
        locationTypeField.setItemLabelGenerator(LocationType::displayName);

        var addressField = new PostalAddressField();
        addressField.setLabel("Address");

        var establishedField = new DatePicker();
        establishedField.setLabel("Established");

        var aboutField = new TextArea();
        aboutField.setLabel("About");
        aboutField.setMaxRows(10);

        var timeZoneField = new TimeZoneField();
        timeZoneField.setLabel("Time zone");

        var facilitiesField = new LocationFacilityField();
        facilitiesField.setLabel("Facilities");

        // Configure the form
        var formLayout = getContent();
        formLayout.add(nameField);
        formLayout.add(locationTypeField);
        formLayout.add(establishedField);
        formLayout.add(timeZoneField);
        formLayout.add(addressField, 2);
        formLayout.add(aboutField, 2);
        formLayout.add(facilitiesField, 2);
        formLayout.setMaxWidth(600, Unit.PIXELS);

        // Setup binder
        binder = new Binder<>(LocationData.class);
        binder.forField(nameField).asRequired().bind(LocationData.PROP_NAME);
        binder.forField(locationTypeField).asRequired().bind(LocationData.PROP_LOCATION_TYPE);
        binder.forField(addressField).asRequired().bind(LocationData.PROP_ADDRESS);
        binder.forField(establishedField).asRequired().bind(LocationData.PROP_ESTABLISHED);
        binder.forField(aboutField).bind(LocationData.PROP_ABOUT);
        binder.forField(timeZoneField).asRequired().bind(LocationData.PROP_TIME_ZONE);
        binder.forField(facilitiesField).bind(LocationData.PROP_FACILITIES);
    }

    public void setFormDataObject(@Nullable LocationData locationData) {
        if (locationData != null) {
            binder.readRecord(locationData);
        } else {
            binder.refreshFields();
        }
    }

    public Optional<LocationData> getFormDataObject() {
        try {
            return Optional.of(binder.writeRecord());
        } catch (ValidationException e) {
            return Optional.empty();
        }
    }
}
