package com.example.whereabouts.projects.ui;

import com.example.whereabouts.common.ui.Notifications;
import com.example.whereabouts.projects.Task;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import org.springframework.dao.OptimisticLockingFailureException;

import java.io.Serializable;

public class EditTaskDialog extends Dialog {

    private final SaveCallback saveCallback;
    private final TaskDataForm form;
    private final Task task;

    public EditTaskDialog(TaskDataForm.AssigneeLookupBySearchTerm assigneeLookupBySearchTerm,
                          TaskDataForm.AssigneeLookupById assigneeLookupById,
                          Task task,
                          SaveCallback saveCallback) {
        this.task = task;
        this.saveCallback = saveCallback;

        form = new TaskDataForm(assigneeLookupBySearchTerm, assigneeLookupById);
        form.setFormDataObject(task.data());

        var saveBtn = new Button("Save", event -> save());
        saveBtn.addThemeName("primary");

        var cancelBtn = new Button("Cancel", event -> close());

        setHeaderTitle("Edit Task");
        setCloseOnOutsideClick(false);
        add(form);
        getFooter().add(cancelBtn, saveBtn);
    }

    private void save() {
        form.getFormDataObject().ifPresent(data -> {
            try {
                saveCallback.save(task.withData(data));
                close();
            } catch (OptimisticLockingFailureException ex) {
                Notifications.createOptimisticLockingFailureNotification().open();
            }
        });
    }

    @FunctionalInterface
    public interface SaveCallback extends Serializable {
        void save(Task task);
    }
}
