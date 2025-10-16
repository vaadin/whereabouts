package com.example.whereabouts.humanresources.ui;

import com.example.whereabouts.humanresources.*;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.Converter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@NullMarked
class EmploymentDetailsDataForm extends Composite<FormLayout> {

    private final Binder<EmploymentDetailsData> binder;

    EmploymentDetailsDataForm(LocationLookupBySearchTerm locationLookupBySearchTerm,
                              LocationLookupById locationLookupById,
                              ManagerLookupBySearchTerm managerLookupBySearchTerm,
                              ManagerLookupById managerLookupById) {
        // Create the components
        var jobTitle = new TextField("Job Title");
        var type = new Select<EmploymentType>("Employment Type");
        type.setItems(EmploymentType.values());
        type.setItemLabelGenerator(EmploymentTypeFormatter.ofLocale(getLocale())::getDisplayName);
        var status = new Select<EmploymentStatus>("Employment Status");
        status.setItems(EmploymentStatus.values());
        status.setItemLabelGenerator(EmploymentStatusFormatter.ofLocale(getLocale())::getDisplayName);
        var workArrangement = new Select<WorkArrangement>("Work Arrangement");
        workArrangement.setItems(WorkArrangement.values());
        workArrangement.setItemLabelGenerator(WorkArrangementFormatter.ofLocale(getLocale())::getDisplayName);
        var location = createLocationField(locationLookupBySearchTerm);
        var manager = createManagerField(managerLookupBySearchTerm);
        var hireDate = new DatePicker("Hire Date");
        var terminationDate = new DatePicker("Termination Date");

        // Configure the form
        var formLayout = getContent();
        formLayout.add(jobTitle);
        formLayout.add(type);
        formLayout.add(status);
        formLayout.add(workArrangement);
        formLayout.add(location);
        formLayout.add(manager);
        formLayout.add(hireDate);
        formLayout.add(terminationDate);

        // Setup binder
        binder = new Binder<>(EmploymentDetailsData.class);
        binder.forField(jobTitle).asRequired("Enter job title").bind(EmploymentDetailsData.PROP_JOB_TITLE);
        binder.forField(type).asRequired("Select employment type").bind(EmploymentDetailsData.PROP_TYPE);
        binder.forField(status).asRequired("Select employment status").bind(EmploymentDetailsData.PROP_STATUS);
        binder.forField(workArrangement).asRequired("Select work arrangement").bind(EmploymentDetailsData.PROP_WORK_ARRANGEMENT);
        binder.forField(location)
                .asRequired("Select primary location")
                .withConverter(createLocationConverter(locationLookupById))
                .bind(EmploymentDetailsData.PROP_LOCATION);
        binder.forField(manager)
                .withConverter(createManagerConverter(managerLookupById))
                .bind(EmploymentDetailsData.PROP_MANAGER);
        binder.forField(hireDate).asRequired("Enter hire date").bind(EmploymentDetailsData.PROP_HIRE_DATE);
        var terminationDateBinding = binder.forField(terminationDate)
                .asRequired("Enter termination date")
                .withValidator(date -> date == null || hireDate.isEmpty() || date.isAfter(hireDate.getValue()), "Termination date must be after hire date")
                .bind(EmploymentDetailsData.PROP_TERMINATION_DATE);

        status.addValueChangeListener(e -> {
            var isTerminated = e.getValue() == EmploymentStatus.TERMINATED;
            terminationDateBinding.setAsRequiredEnabled(isTerminated);
            terminationDate.setVisible(isTerminated);
            if (!isTerminated) {
                terminationDate.clear();
            }
        });
        hireDate.addValueChangeListener(e -> terminationDateBinding.validate());
    }

    private static ComboBox<EmployeeReference> createManagerField(ManagerLookupBySearchTerm managerLookupBySearchTerm) {
        var managerField = new ComboBox<EmployeeReference>("Manager");
        var nameFormatter = PersonNameFormatter.firstLast();
        managerField.setItemLabelGenerator(nameFormatter::toFullName);
        managerField.setItemsPageable(managerLookupBySearchTerm::findBySearchTerm);
        return managerField;
    }

    @NullUnmarked
    private static @NonNull Converter<EmployeeReference, EmployeeId> createManagerConverter(@NonNull ManagerLookupById managerLookupById) {
        return Converter.from(
                employeeReference -> Result.ok(Optional.ofNullable(employeeReference).map(EmployeeReference::id).orElse(null)),
                employeeId -> Optional.ofNullable(employeeId).flatMap(managerLookupById::findById).orElse(null)
        );
    }

    private static ComboBox<LocationReference> createLocationField(LocationLookupBySearchTerm locationLookupBySearchTerm) {
        var locationField = new ComboBox<LocationReference>("Location");
        locationField.setItemLabelGenerator(location -> String.format("%s %s", location.name(),
                location.country().flagUnicode()));
        locationField.setItemsPageable(locationLookupBySearchTerm::findBySearchTerm);
        return locationField;
    }

    @NullUnmarked
    private static @NonNull Converter<LocationReference, LocationId> createLocationConverter(@NonNull LocationLookupById locationLookupById) {
        return Converter.from(
                locationReference -> Result.ok(Optional.ofNullable(locationReference).map(LocationReference::id).orElse(null)),
                locationId -> Optional.ofNullable(locationId).flatMap(locationLookupById::findById).orElse(null)
        );
    }

    public void setFormDataObject(@Nullable EmploymentDetailsData data) {
        if (data != null) {
            binder.readRecord(data);
        } else {
            binder.refreshFields();
        }
    }

    public Optional<EmploymentDetailsData> getFormDataObject() {
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

    @FunctionalInterface
    public interface ManagerLookupBySearchTerm {
        List<EmployeeReference> findBySearchTerm(Pageable pageable, @Nullable String searchTerm);
    }

    @FunctionalInterface
    public interface ManagerLookupById {
        Optional<EmployeeReference> findById(EmployeeId id);
    }

    @FunctionalInterface
    public interface LocationLookupBySearchTerm {
        List<LocationReference> findBySearchTerm(Pageable pageable, @Nullable String searchTerm);
    }

    @FunctionalInterface
    public interface LocationLookupById {
        Optional<LocationReference> findById(LocationId id);
    }
}
