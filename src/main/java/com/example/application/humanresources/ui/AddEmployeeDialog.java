package com.example.application.humanresources.ui;

import com.example.application.humanresources.EmployeeData;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import org.jspecify.annotations.NullMarked;

import java.io.Serializable;

@NullMarked
class AddEmployeeDialog extends Dialog {

    private final SaveCallback saveCallback;
    private final EmployeeDataForm form;

    AddEmployeeDialog(SaveCallback saveCallback) {
        this.saveCallback = saveCallback;

        // Create the components
        form = new EmployeeDataForm();

        var saveBtn = new Button("Create Employee", e -> save());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelBtn = new Button("Cancel", e -> close());

        // Configure the dialog
        setHeaderTitle("New Employee");
        setCloseOnOutsideClick(false);
        add(form);
        getFooter().add(cancelBtn, saveBtn);
        setMaxWidth("650px");
    }

    private void save() {
        form.getFormDataObject().ifPresent(employeeData -> {
            saveCallback.save(employeeData);
            close();
        });
    }

    @FunctionalInterface
    public interface SaveCallback extends Serializable {
        void save(EmployeeData employeeData);
    }
}
