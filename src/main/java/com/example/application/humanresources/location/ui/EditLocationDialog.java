package com.example.application.humanresources.location.ui;


import com.example.application.common.ui.AppIcon;
import com.example.application.common.ui.Notifications;
import com.example.application.humanresources.location.Location;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.OptimisticLockingFailureException;

import java.io.Serializable;

@NullMarked
class EditLocationDialog extends Dialog {

    private final SaveCallback saveCallback;
    private final LocationDataForm form;
    private final Location location;

    EditLocationDialog(Location location, SaveCallback saveCallback) {
        this.location = location;
        this.saveCallback = saveCallback;

        form = new LocationDataForm();
        form.setFormDataObject(location.data());

        var saveBtn = new Button("Save", e -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", e -> close());

        setHeaderTitle("Edit Location");
        setCloseOnOutsideClick(false);
        add(form);
        getFooter().add(cancelBtn, saveBtn);
    }

    private void save() {
        form.getFormDataObject().ifPresent(locationData -> {
            try {
                saveCallback.save(location.withData(locationData));
                close();
            } catch (OptimisticLockingFailureException e) {
                Notifications.createCriticalNotification(AppIcon.PERSON_PLAY.create(),
                        "Another user has edited the location. Please refresh and try again.",
                        NotificationVariant.LUMO_WARNING).open();
            }
        });
    }

    @FunctionalInterface
    public interface SaveCallback extends Serializable {

        /**
         *
         * @param location
         * @throws OptimisticLockingFailureException
         */
        void save(Location location);
    }
}
