package com.example.application.taskmanagement.ui.component;

import com.example.application.security.AppUserInfo;
import com.example.application.security.AppUserInfoLookup;
import com.example.application.security.domain.UserId;
import com.example.application.taskmanagement.domain.Task;
import com.example.application.taskmanagement.domain.TaskPriority;
import com.example.application.taskmanagement.domain.TaskStatus;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

import java.time.format.TextStyle;
import java.util.Optional;
import java.util.stream.Stream;

public class TaskForm extends Composite<FormLayout> {

    private final Binder<Task> binder;
    private final TimePicker dueTimeField;
    private Task formDataObject;

    public TaskForm(AppUserInfoLookup appUserInfoLookup, Task initialFormDataObject) {
        var descriptionField = new TextField("Description");
        descriptionField.setPlaceholder("Enter a description");

        var dueDateField = new DatePicker("Due Date");

        dueTimeField = new TimePicker("Due Time");

        var statusField = new ComboBox<TaskStatus>("Status");
        statusField.setItems(TaskStatus.values());
        statusField.setItemLabelGenerator(TaskStatus::getDisplayName);

        var priorityField = new ComboBox<TaskPriority>("Priority");
        priorityField.setItems(TaskPriority.values());
        priorityField.setItemLabelGenerator(TaskPriority::getDisplayName);

        var assigneesField = createAssigneesField(appUserInfoLookup);

        var formLayout = getContent();
        formLayout.setMaxWidth("640px");
        formLayout.add(descriptionField, 2);
        formLayout.add(dueDateField, dueTimeField);
        formLayout.add(statusField, priorityField);
        formLayout.add(assigneesField, 2);

        binder = new Binder<>();
        binder.forField(descriptionField).asRequired()
                .withValidator(new StringLengthValidator("Description is too long", 0, Task.DESCRIPTION_MAX_LENGTH))
                .bind(Task::getDescription, Task::setDescription);
        var dueDateFieldBinding = binder.forField(dueDateField)
                .withValidator(date -> date != null || dueTimeField.isEmpty(), "Specify a due date")
                .bind(Task::getDueDate, Task::setDueDate);
        binder.forField(dueTimeField)
                .bind(Task::getDueTime, Task::setDueTime);
        dueTimeField.addValueChangeListener(event -> dueDateFieldBinding.validate());
        binder.forField(statusField).asRequired().bind(Task::getStatus, Task::setStatus);
        binder.forField(priorityField).asRequired().bind(Task::getPriority, Task::setPriority);
        binder.forField(assigneesField).bind(Task::getAssignees, Task::setAssignees);

        setFormDataObject(initialFormDataObject);
    }

    private static MultiSelectComboBox<UserId> createAssigneesField(AppUserInfoLookup appUserInfoLookup) {
        var assigneesField = new MultiSelectComboBox<UserId>("Assignees");
        assigneesField.setItemLabelGenerator(
                userId -> appUserInfoLookup.findUserInfo(userId).map(AppUserInfo::getFullName).orElse("N/A"));
        assigneesField.setItems(query -> query.getFilter().map(searchTerm -> appUserInfoLookup
                        .findUsers(searchTerm, query.getLimit(), query.getOffset()).stream().map(AppUserInfo::getUserId))
                .orElse(Stream.empty()));
        return assigneesField;
    }

    public void setFormDataObject(Task formDataObject) {
        this.formDataObject = formDataObject;
        dueTimeField.setHelperText(formDataObject.getTimeZone().getDisplayName(TextStyle.NARROW, getLocale()));
        binder.readBean(formDataObject);
    }

    public Optional<Task> getFormDataObject() {
        if (binder.writeBeanIfValid(formDataObject)) {
            return Optional.of(formDataObject);
        } else {
            return Optional.empty();
        }
    }
}
