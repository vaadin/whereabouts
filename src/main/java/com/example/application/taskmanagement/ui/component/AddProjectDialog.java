package com.example.application.taskmanagement.ui.component;

import com.example.application.taskmanagement.dto.ProjectFormDataObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.function.SerializableConsumer;

public class AddProjectDialog extends Dialog {

    private final SerializableConsumer<ProjectFormDataObject> onSaveCallback;
    private final ProjectForm form;

    public AddProjectDialog(SerializableConsumer<ProjectFormDataObject> onSaveCallback) {
        this.onSaveCallback = onSaveCallback;

        form = new ProjectForm();

        var saveBtn = new Button("Save", event -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", event -> close());

        setHeaderTitle("Add Project");
        add(form);
        getFooter().add(cancelBtn, saveBtn);
    }

    private void save() {
        form.getFormDataObject().ifPresent(project -> {
            onSaveCallback.accept(project);
            close();
        });
    }
}
