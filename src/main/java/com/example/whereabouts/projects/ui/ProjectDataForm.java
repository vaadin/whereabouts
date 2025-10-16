package com.example.whereabouts.projects.ui;

import com.example.whereabouts.projects.ProjectData;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
class ProjectDataForm extends Composite<FormLayout> {

    private final Binder<ProjectData> binder;

    ProjectDataForm() {
        // Create the components
        var nameField = new TextField("Name");
        nameField.setPlaceholder("Enter a name");

        var descriptionField = new TextArea("Description");
        descriptionField.setPlaceholder("Enter a description");

        // Configure the form
        var formLayout = getContent();
        formLayout.setMinWidth("400px");
        formLayout.add(nameField);
        formLayout.add(descriptionField);

        // Setup binder
        binder = new Binder<>(ProjectData.class);
        binder.forField(nameField)
                .asRequired()
                .withValidator(new StringLengthValidator("Project name is too long", 0, ProjectData.NAME_MAX_LENGTH))
                .bind(ProjectData.PROP_NAME);
        binder.forField(descriptionField).bind(ProjectData.PROP_DESCRIPTION);
    }

    public Optional<ProjectData> getFormDataObject() {
        try {
            return Optional.of(binder.writeRecord());
        } catch (ValidationException e) {
            return Optional.empty();
        }
    }
}
