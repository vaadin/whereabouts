package com.example.application.humanresources.ui;

import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmployeeReference;
import com.example.application.humanresources.PersonNameFormatter;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.server.streams.DownloadHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@NullMarked
class EmployeeTitleCard extends Card {

    public EmployeeTitleCard(String firstName, String lastName, @Nullable String title, @Nullable DownloadHandler picture) {
        var fullName = PersonNameFormatter.firstLast().toFullName(firstName, lastName);
        var avatar = new Avatar(fullName);
        if (picture != null) {
            avatar.setImageHandler(picture);
        }
        setHeaderPrefix(avatar);
        setTitle(fullName);
        if (title != null) {
            setSubtitle(new Div(title));
        }
    }

    public static EmployeeTitleCard of(EmployeeReference employee, Function<EmployeeId, @Nullable DownloadHandler> pictureProvider) {
        return new EmployeeTitleCard(employee.firstName(), employee.lastName(), employee.title(), pictureProvider.apply(employee.id()));
    }
}
