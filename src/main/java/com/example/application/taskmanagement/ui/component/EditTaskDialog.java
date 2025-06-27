package com.example.application.taskmanagement.ui.component;

import com.example.application.security.AppUserInfoLookup;
import com.example.application.taskmanagement.Task;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.function.SerializableConsumer;

public class EditTaskDialog extends Dialog {

    private final SerializableConsumer<Task> onSaveCallback;
    private final TaskForm form;

    public EditTaskDialog(AppUserInfoLookup appUserInfoLookup, Task formDataObject, SerializableConsumer<Task> onSaveCallback) {
        this.onSaveCallback = onSaveCallback;

        form = new TaskForm(appUserInfoLookup, formDataObject);

        var saveBtn = new Button("Save", event -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", event -> close());

        setHeaderTitle("Edit Task");
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
