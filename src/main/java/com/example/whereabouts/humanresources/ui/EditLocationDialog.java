package com.example.whereabouts.humanresources.ui;


import com.example.whereabouts.common.ui.Notifications;
import com.example.whereabouts.humanresources.Location;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
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
        saveBtn.addThemeName("primary");

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
                Notifications.createOptimisticLockingFailureNotification().open();
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
