package com.example.application.humanresources;

import com.example.application.common.Country;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record LocationReference(LocationId id, String name, Country country) {
}
