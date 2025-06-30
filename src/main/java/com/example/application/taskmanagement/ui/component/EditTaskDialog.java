package com.example.application.taskmanagement.ui.component;

import com.example.application.base.ui.component.Notifications;
import com.example.application.security.AppUserInfoLookup;
import com.example.application.taskmanagement.domain.Task;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.function.SerializableConsumer;
import org.springframework.dao.OptimisticLockingFailureException;

public class EditTaskDialog extends Dialog {

    private final SerializableConsumer<Task> onSaveCallback;
    private final TaskForm form;

    public EditTaskDialog(AppUserInfoLookup appUserInfoLookup, Task formDataObject,
            SerializableConsumer<Task> onSaveCallback) {
        this.onSaveCallback = onSaveCallback;

        form = new TaskForm(appUserInfoLookup, formDataObject);

        var saveBtn = new Button("Save", event -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", event -> close());

        setHeaderTitle("Edit Task");
        setCloseOnOutsideClick(false);
        add(form);
        getFooter().add(cancelBtn, saveBtn);
    }

    private void save() {
        form.getFormDataObject().ifPresent(project -> {
            try {
                onSaveCallback.accept(project);
                close();
            } catch (OptimisticLockingFailureException ex) {
                Notifications.createCriticalNotification(new SvgIcon("icons/person_play.svg"),
                        "Another user has edited the task. Please refresh and try again.",
                        NotificationVariant.LUMO_WARNING).open();
            }
        });
    }
}
