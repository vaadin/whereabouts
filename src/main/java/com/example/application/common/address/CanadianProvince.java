package com.example.application.common.address;

import com.example.application.common.ValueObject;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum CanadianProvince implements ValueObject {
    AB("Alberta", true),
    BC("British Columbia", true),
    MB("Manitoba", true),
    NB("New Brunswick", true),
    NL("Newfoundland and Labrador", true),
    NS("Nova Scotia", true),
    ON("Ontario", true),
    PE("Prince Edward Island", true),
    QC("Quebec", true),
    SK("Saskatchewan", true),
    NT("Northwest Territories", false),
    NU("Nunavut", false),
    YT("Yukon", false);

    private final String displayName;
    private final boolean isProvince;

    CanadianProvince(String displayName, boolean isProvince) {
        this.displayName = displayName;
        this.isProvince = isProvince;
    }

    public boolean isProvince() {
        return isProvince;
    }

    public String displayName() {
        return displayName;
    }
}
