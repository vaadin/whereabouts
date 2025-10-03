package com.example.application.humanresources.location;

import com.example.application.common.Country;
import com.example.application.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface LocationTreeNode {

    record CountryNode(Country country, int employees) implements LocationTreeNode {
    }

    record LocationNode(LocationId id, String name, int employees, LocationType locationType,
                        PostalAddress address) implements LocationTreeNode {
    }
}
