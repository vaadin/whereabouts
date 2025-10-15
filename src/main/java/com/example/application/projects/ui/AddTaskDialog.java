package com.example.application.projects.ui;

import com.example.application.projects.TaskData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

@NullMarked
class AddTaskDialog extends Dialog {

    private final SaveCallback saveCallback;
    private final TaskDataForm form;

    AddTaskDialog(TaskDataForm.AssigneeLookupBySearchTerm assigneeLookupBySearchTerm,
                  TaskDataForm.AssigneeLookupById assigneeLookupById,
                  TaskData initialTaskData,
                  SaveCallback saveCallback) {
        this.saveCallback = saveCallback;

        form = new TaskDataForm(assigneeLookupBySearchTerm, assigneeLookupById);
        form.setFormDataObject(initialTaskData);

        var saveBtn = new Button("Save", event -> save());
        saveBtn.addThemeName("primary");

        var cancelBtn = new Button("Cancel", event -> close());

        setHeaderTitle("Add Task");
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
        void save(TaskData data);
    }
}
