package com.example.whereabouts.humanresources;

import com.example.whereabouts.common.Country;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record LocationReference(LocationId id, String name, Country country) {
}
