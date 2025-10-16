package com.example.whereabouts.humanresources.ui;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.humanresources.EmployeeReference;
import com.example.whereabouts.humanresources.PersonNameFormatter;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.streams.DownloadHandler;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

@NullMarked
class EmployeeTitleCard extends Card {

    public EmployeeTitleCard(@Nullable String firstName, @Nullable String middleName, @Nullable String lastName, @Nullable Country country, @Nullable String title, @Nullable DownloadHandler picture) {
        var fullName = PersonNameFormatter.firstLast().toFullName(firstName, middleName, lastName);
        var avatar = new Avatar(fullName);
        if (picture != null) {
            avatar.setImageHandler(picture);
        }
        setHeaderPrefix(avatar);
        if (country != null) {
            setHeaderSuffix(new Span(country.flagUnicode()));
        }
        setTitle(fullName);
        if (title != null) {
            setSubtitle(new Div(title));
        }
    }

    public static EmployeeTitleCard of(EmployeeReference employee, Function<EmployeeId, @Nullable DownloadHandler> pictureProvider) {
        return new EmployeeTitleCard(employee.firstName(), employee.middleName(), employee.lastName(),
                employee.country(), employee.title(), pictureProvider.apply(employee.id()));
    }
}
