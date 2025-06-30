package com.example.application.taskmanagement.ui.component;

import com.example.application.taskmanagement.domain.Project;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ProjectForm extends Composite<FormLayout> {

    private final Binder<Project> binder;
    private @Nullable Project formDataObject;

    public ProjectForm() {
        var nameField = new TextField("Name");
        nameField.setPlaceholder("Enter a name");

        var formLayout = getContent();
        formLayout.setMinWidth("400px");
        formLayout.add(nameField);

        binder = new Binder<>(Project.class);
        binder.forField(nameField)
                .asRequired()
                .withValidator(new StringLengthValidator("Project name is too long", 0, Project.NAME_MAX_LENGTH))
                .bind(Project::getName, Project::setName);
    }

    public void setFormDataObject(@Nullable Project formDataObject) {
        this.formDataObject = formDataObject;
        if (formDataObject == null) {
            binder.refreshFields();
        } else {
            binder.readBean(formDataObject);
        }
    }

    public Optional<Project> getFormDataObject() {
        if (formDataObject == null) {
            formDataObject = new Project();
        }
        if (binder.writeBeanIfValid(formDataObject)) {
            return Optional.of(formDataObject);
        } else {
            return Optional.empty();
        }
    }
}
