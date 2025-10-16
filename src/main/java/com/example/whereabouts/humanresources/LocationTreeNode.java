package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Country;
import com.example.whereabouts.common.address.PostalAddress;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface LocationTreeNode {

    record CountryNode(Country country, int employees) implements LocationTreeNode {
    }

    record LocationNode(LocationId id, String name, int employees, LocationType locationType,
                        PostalAddress address) implements LocationTreeNode {
    }
}
