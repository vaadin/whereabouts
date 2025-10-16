package com.example.whereabouts.humanresources.ui;

import com.example.whereabouts.common.EmailAddress;
import com.example.whereabouts.common.Gender;
import com.example.whereabouts.common.PhoneNumber;
import com.example.whereabouts.common.ui.PostalAddressField;
import com.example.whereabouts.common.ui.TimeZoneField;
import com.example.whereabouts.common.ui.ValueObjectStringConverter;
import com.example.whereabouts.humanresources.EmployeeData;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.dom.ElementFactory;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@NullMarked
class EmployeeDataForm extends Composite<FormLayout> {

    private final Binder<EmployeeData> binder;

    EmployeeDataForm() {
        // Create the components
        var firstNameField = new TextField("First Name");
        var middleNameField = new TextField("Middle Name");
        var lastNameField = new TextField("Last Name");
        var preferredNameField = new TextField("Preferred Name");
        var birthDateField = new DatePicker("Birth Date");
        var genderField = new Select<Gender>("Gender");
        genderField.setItems(Gender.values());
        genderField.setItemLabelGenerator(gender -> StringUtils.capitalize(gender.name().toLowerCase()));
        var dietaryNotesField = new TextArea("Dietary Notes");
        var homeAddressField = new PostalAddressField();
        homeAddressField.setLabel("Home Address");
        var timeZoneField = new TimeZoneField();
        timeZoneField.setLabel("Time Zone");
        var workPhoneField = new TextField("Work Phone");
        var mobilePhoneField = new TextField("Mobile Phone");
        var homePhoneField = new TextField("Home Phone");
        var workEmailField = new EmailField("Work Email"); // TODO Should include a unique validator

        // Configure the form
        var formLayout = getContent();
        formLayout.setMaxColumns(4);

        formLayout.add(createSubheader("Basic Information"), 4);
        formLayout.add(firstNameField);
        formLayout.add(middleNameField);
        formLayout.add(lastNameField);
        formLayout.add(preferredNameField);
        formLayout.add(birthDateField);
        formLayout.add(genderField);
        formLayout.add(dietaryNotesField, 4);

        formLayout.getElement().appendChild(ElementFactory.createBr());
        formLayout.add(createSubheader("Location"), 4);
        formLayout.add(homeAddressField, 3);
        formLayout.add(timeZoneField);

        formLayout.getElement().appendChild(ElementFactory.createBr());
        formLayout.add(createSubheader("Contact Information"), 4);
        formLayout.add(workPhoneField);
        formLayout.add(mobilePhoneField);
        formLayout.add(homePhoneField);
        formLayout.add(workEmailField);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("400px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("1000px", 3),
                new FormLayout.ResponsiveStep("1600px", 4)
        );

        // Setup binder
        binder = new Binder<>(EmployeeData.class);
        binder.forField(firstNameField).asRequired("Enter first name").bind(EmployeeData.PROP_FIRST_NAME);
        binder.forField(middleNameField).bind(EmployeeData.PROP_MIDDLE_NAME);
        binder.forField(lastNameField).asRequired("Enter last name").bind(EmployeeData.PROP_LAST_NAME);
        binder.forField(preferredNameField).asRequired("Enter preferred name").bind(EmployeeData.PROP_PREFERRED_NAME);
        binder.forField(birthDateField).asRequired("Enter birth date").bind(EmployeeData.PROP_BIRTH_DATE);
        binder.forField(genderField).asRequired("Select gender").bind(EmployeeData.PROP_GENDER);
        binder.forField(dietaryNotesField).bind(EmployeeData.PROP_DIETARY_NOTES);
        binder.forField(homeAddressField).asRequired("Enter home address").bind(EmployeeData.PROP_HOME_ADDRESS);
        binder.forField(timeZoneField).asRequired("Select time zone").bind(EmployeeData.PROP_TIME_ZONE);
        binder.forField(workPhoneField)
                .withConverter(new ValueObjectStringConverter<>(PhoneNumber::of))
                .bind(EmployeeData.PROP_WORK_PHONE);
        binder.forField(mobilePhoneField)
                .withConverter(new ValueObjectStringConverter<>(PhoneNumber::of))
                .bind(EmployeeData.PROP_MOBILE_PHONE);
        binder.forField(homePhoneField)
                .withConverter(new ValueObjectStringConverter<>(PhoneNumber::of))
                .bind(EmployeeData.PROP_HOME_PHONE);
        binder.forField(workEmailField)
                .asRequired("Enter work email")
                .withConverter(new ValueObjectStringConverter<>(EmailAddress::of))
                .bind(EmployeeData.PROP_WORK_EMAIL);
    }

    private Component createSubheader(String subheader) {
        var h = new H3(subheader);
        h.getStyle().setMarginTop("var(--vaadin-gap-m)");
        return h;
    }

    public void setFormDataObject(@Nullable EmployeeData employeeData) {
        if (employeeData != null) {
            binder.readRecord(employeeData);
        } else {
            binder.refreshFields();
        }
    }

    public Optional<EmployeeData> getFormDataObject() {
        try {
            return Optional.of(binder.writeRecord());
        } catch (ValidationException e) {
            return Optional.empty();
        }
    }

    public void setReadOnly(boolean readOnly) {
        getContent().getChildren().forEach(child -> {
            if (child instanceof HasValue<?, ?> field) {
                field.setReadOnly(readOnly);
            }
        });
    }
}
