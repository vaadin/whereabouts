package com.example.application.common.address;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum USState {
    AL("Alabama", true),
    AK("Alaska", true),
    AZ("Arizona", true),
    AR("Arkansas", true),
    CA("California", true),
    CO("Colorado", true),
    CT("Connecticut", true),
    DE("Delaware", true),
    FL("Florida", true),
    GA("Georgia", true),
    HI("Hawaii", true),
    ID("Idaho", true),
    IL("Illinois", true),
    IN("Indiana", true),
    IA("Iowa", true),
    KS("Kansas", true),
    KY("Kentucky", true),
    LA("Louisiana", true),
    ME("Maine", true),
    MD("Maryland", true),
    MA("Massachusetts", true),
    MI("Michigan", true),
    MN("Minnesota", true),
    MS("Mississippi", true),
    MO("Missouri", true),
    MT("Montana", true),
    NE("Nebraska", true),
    NV("Nevada", true),
    NH("New Hampshire", true),
    NJ("New Jersey", true),
    NM("New Mexico", true),
    NY("New York", true),
    NC("North Carolina", true),
    ND("North Dakota", true),
    OH("Ohio", true),
    OK("Oklahoma", true),
    OR("Oregon", true),
    PA("Pennsylvania", true),
    RI("Rhode Island", true),
    SC("South Carolina", true),
    SD("South Dakota", true),
    TN("Tennessee", true),
    TX("Texas", true),
    UT("Utah", true),
    VT("Vermont", true),
    VA("Virginia", true),
    WA("Washington", true),
    WV("West Virginia", true),
    WI("Wisconsin", true),
    WY("Wyoming", true),
    DC("District of Columbia", false),
    PR("Puerto Rico", false),
    GU("Guam", false),
    VI("U.S. Virgin Islands", false),
    AS("American Samoa", false),
    MP("Northern Mariana Islands", false);

    private final String displayName;
    private final boolean isState;

    USState(String displayName, boolean isState) {
        this.displayName = displayName;
        this.isState = isState;
    }

    public boolean isState() {
        return isState;
    }

    public String displayName() {
        return displayName;
    }
}