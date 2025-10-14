package com.example.application.common.ui;

import com.vaadin.flow.component.icon.SvgIcon;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public enum AppIcon {
    ACCESSIBLE("icons/accessible.svg"),
    APARTMENT("icons/apartment.svg"),
    CALENDAR_MONTH("icons/calendar_month.svg"),
    CHECK("icons/check.svg"),
    CLOSE("icons/close.svg"),
    DELETE_SWEEP("icons/delete_sweep.svg"),
    DESK("icons/desk.svg"),
    DIVERSITY("icons/diversity.svg"),
    ERROR("icons/error.svg"),
    FILTER_LIST("icons/filter_list.svg"),
    FILTER_NONE("icons/filter_none.svg"),
    FLATWARE("icons/flatware.svg"),
    FOLDER_CHECK_2("icons/folder_check_2.svg"),
    GLOBE_LOCATION_PIN("icons/globe_location_pin.svg"),
    LIST_ALT_CHECK("icons/list_alt_check.svg"),
    MEETING_ROOM("icons/meeting_room.svg"),
    MORE_VERT("icons/more_vert.svg"),
    PARKING_SIGN("icons/parking_sign.svg"),
    PERSON_PLAY("icons/person_play.svg"),
    REFRESH("icons/refresh.svg"),
    SEARCH("icons/search.svg");

    private final String source;

    AppIcon(String source) {
        this.source = source;
    }

    public SvgIcon create() {
        return create(Size.UNDEFINED);
    }

    public SvgIcon create(Size size) {
        return create(size, Color.UNDEFINED);
    }

    public SvgIcon create(Size size, Color color) {
        var icon = new SvgIcon(source);
        icon.setSize(size.size);
        icon.setColor(color.color);
        return icon;
    }

    public enum Color {
        UNDEFINED(null),
        BLUE("var(--aura-blue)"),
        RED("var(--aura-red)"),
        GREEN("var(--aura-green)"),
        YELLOW("var(--aura-yellow)"),
        ORANGE("var(--aura-orange)"),
        PURPLE("var(--aura-purple)");

        private final @Nullable String color;

        Color(@Nullable String color) {
            this.color = color;
        }
    }

    public enum Size {
        UNDEFINED(null),
        XS("16px"),
        S("24px"),
        M("32px"),
        L("48px"),
        XL("60px");

        private final @Nullable String size;

        Size(@Nullable String size) {
            this.size = size;
        }
    }
}
