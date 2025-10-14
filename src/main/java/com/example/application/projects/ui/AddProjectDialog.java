package com.example.application.projects.ui;

import com.example.application.projects.ProjectData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

@NullMarked
class AddProjectDialog extends Dialog {

    private final SaveCallback saveCallback;
    private final ProjectDataForm form;

    public AddProjectDialog(SaveCallback saveCallback) {
        this.saveCallback = saveCallback;

        form = new ProjectDataForm();

        var saveBtn = new Button("Save", event -> save());
        saveBtn.addThemeName("primary");

        var cancelBtn = new Button("Cancel", event -> close());

        setHeaderTitle("Add Project");
        add(form);
        getFooter().add(cancelBtn, saveBtn);
    }

    private void save() {
        form.getFormDataObject().ifPresent(data -> {
            saveCallback.save(data);
            close();
        });
    }

    @FunctionalInterface
    public interface SaveCallback extends Serializable {
        void save(ProjectData data);
    }
}
