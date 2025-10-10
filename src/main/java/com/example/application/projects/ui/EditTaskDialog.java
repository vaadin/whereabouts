package com.example.application.projects.ui;

import com.example.application.common.ui.AppIcon;
import com.example.application.common.ui.Notifications;
import com.example.application.projects.Task;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.dao.OptimisticLockingFailureException;

import java.io.Serializable;

public class EditTaskDialog extends Dialog {

    private final SaveCallback saveCallback;
    private final TaskDataForm form;
    private final Task task;

    public EditTaskDialog(TaskDataForm.AssigneeLookupBySearchTerm assigneeLookupBySearchTerm,
                          Task task,
                          SaveCallback saveCallback) {
        this.task = task;
        this.saveCallback = saveCallback;

        form = new TaskDataForm(assigneeLookupBySearchTerm);
        form.setFormDataObject(task.data());

        var saveBtn = new Button("Save", event -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

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
                Notifications.createCriticalNotification(AppIcon.PERSON_PLAY.create(),
                        "Another user has edited the task. Please refresh and try again.",
                        NotificationVariant.LUMO_WARNING).open();
            }
        });
    }

    @FunctionalInterface
    public interface SaveCallback extends Serializable {
        void save(Task task);
    }
}
