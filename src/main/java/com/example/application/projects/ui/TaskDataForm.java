package com.example.application.projects.ui;

import com.example.application.humanresources.EmployeeId;
import com.example.application.projects.*;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
class TaskDataForm extends Composite<FormLayout> {

    private final Binder<TaskData> binder;
    private final TimePicker dueTimeField;

    TaskDataForm(AssigneeLookupBySearchTerm assigneeLookupBySearchTerm, AssigneeLookupById assigneeLookupById) {
        // Create the components
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

        var assigneesField = createAssigneesField(assigneeLookupBySearchTerm);

        // Configure the form
        var formLayout = getContent();
        formLayout.setMaxWidth("640px");
        formLayout.add(descriptionField, 2);
        formLayout.add(dueDateField, dueTimeField);
        formLayout.add(statusField, priorityField);
        formLayout.add(assigneesField, 2);

        // Setup binder
        binder = new Binder<>(TaskData.class);
        binder.forField(new ReadOnlyHasValue<ProjectId>(ignore -> {
        })).bind(TaskData.PROP_PROJECT);
        binder.forField(new ReadOnlyHasValue<ZoneId>(ignore -> {
        })).bind(TaskData.PROP_TIMEZONE);
        binder.forField(descriptionField)
                .asRequired()
                .withValidator(new StringLengthValidator("Description is too long", 0, TaskData.DESCRIPTION_MAX_LENGTH))
                .bind(TaskData.PROP_DESCRIPTION);
        var dueDateFieldBinding = binder.forField(dueDateField)
                .withValidator(date -> date != null || dueTimeField.isEmpty(), "Specify a due date")
                .bind(TaskData.PROP_DUE_DATE);
        binder.forField(dueTimeField).bind(TaskData.PROP_DUE_TIME);
        dueTimeField.addValueChangeListener(event -> dueDateFieldBinding.validate());
        binder.forField(statusField).asRequired().bind(TaskData.PROP_STATUS);
        binder.forField(priorityField).asRequired().bind(TaskData.PROP_PRIORITY);
        binder.forField(assigneesField)
                .withConverter(createAssigneesConverter(assigneeLookupById))
                .bind(TaskData.PROP_ASSIGNEES);
    }

    private static MultiSelectComboBox<TaskAssignee> createAssigneesField(AssigneeLookupBySearchTerm assigneeLookupBySearchTerm) {
        var assigneesField = new MultiSelectComboBox<TaskAssignee>("Assignees");
        assigneesField.setItemLabelGenerator(TaskAssignee::displayName);
        assigneesField.setItemsPageable(assigneeLookupBySearchTerm::findAssignees);
        return assigneesField;
    }

    private Converter<Set<TaskAssignee>, Set<EmployeeId>> createAssigneesConverter(AssigneeLookupById assigneeLookupById) {
        return Converter.from(
                assignees -> Result.ok(assignees.stream().map(TaskAssignee::id).collect(Collectors.toSet())),
                assigneeLookupById::findAssignees
        );
    }

    public void setFormDataObject(TaskData taskData) {
        dueTimeField.setHelperText(taskData.timeZone().getDisplayName(TextStyle.NARROW, getLocale()));
        binder.readBean(taskData);
    }

    public Optional<TaskData> getFormDataObject() {
        try {
            return Optional.of(binder.writeRecord());
        } catch (ValidationException e) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    public interface AssigneeLookupBySearchTerm {
        List<TaskAssignee> findAssignees(Pageable pageable, @Nullable String searchTerm);
    }

    @FunctionalInterface
    public interface AssigneeLookupById {
        Set<TaskAssignee> findAssignees(Set<EmployeeId> ids);
    }
}
