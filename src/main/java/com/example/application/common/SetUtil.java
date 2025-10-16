package com.example.application.common;

import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

@NullMarked
public final class SetUtil {

    private SetUtil() {
    }

    public static <T> Set<T> add(Set<T> items, T itemToAdd) {
        if (items.contains(itemToAdd)) {
            return items;
        }
        var newSet = new HashSet<>(items);
        newSet.add(itemToAdd);
        return newSet;
    }

    public static <T> Set<T> remove(Set<T> items, T itemToRemove) {
        if (!items.contains(itemToRemove)) {
            return items;
        }
        var newSet = new HashSet<>(items);
        newSet.remove(itemToRemove);
        return newSet;
    }
}
