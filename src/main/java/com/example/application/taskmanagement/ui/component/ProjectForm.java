package com.example.application.taskmanagement.ui.component;

import com.example.application.taskmanagement.dto.ProjectFormDataObject;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ProjectForm extends Composite<FormLayout> {

    private final Binder<ProjectFormDataObject> binder;

    public ProjectForm() {
        var nameField = new TextField("Name");
        nameField.setPlaceholder("Enter a name");

        var formLayout = getContent();
        formLayout.setMinWidth("400px");
        formLayout.add(nameField);

        binder = new Binder<>(ProjectFormDataObject.class);
        binder.forField(nameField)
                .asRequired()
                .withValidator(new StringLengthValidator("Project name is too long", 0, ProjectFormDataObject.MAX_NAME_LENGTH))
                .bind(ProjectFormDataObject.PROP_NAME);
    }

    public void setFormDataObject(@Nullable ProjectFormDataObject formDataObject) {
        if (formDataObject != null) {
            binder.readRecord(formDataObject);
        } else {
            binder.refreshFields();
        }
    }

    public Optional<ProjectFormDataObject> getFormDataObject() {
        try {
            return Optional.of(binder.writeRecord());
        } catch (ValidationException ex) {
            return Optional.empty();
        }
    }
}
