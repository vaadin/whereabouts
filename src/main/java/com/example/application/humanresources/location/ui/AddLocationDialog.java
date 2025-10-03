package com.example.application.humanresources.location.ui;

import com.example.application.humanresources.location.LocationData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

@NullMarked
class AddLocationDialog extends Dialog {

    private final SaveCallback saveCallback;
    private final LocationDataForm form;

    AddLocationDialog(SaveCallback saveCallback) {
        this.saveCallback = saveCallback;

        // Create the components
        form = new LocationDataForm();

        var saveBtn = new Button("Create Location", e -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", e -> close());

        // Configure the dialog
        setHeaderTitle("New Location");
        setCloseOnOutsideClick(false);
        add(form);
        getFooter().add(cancelBtn, saveBtn);
    }

    private void save() {
        form.getFormDataObject().ifPresent(locationData -> {
            saveCallback.save(locationData);
            close();
        });
    }

    @FunctionalInterface
    public interface SaveCallback extends Serializable {
        void save(LocationData locationData);
    }
}
