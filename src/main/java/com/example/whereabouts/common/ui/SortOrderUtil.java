package com.example.whereabouts.common.ui;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortOrder;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Function;

@NullMarked
public final class SortOrderUtil {

    private SortOrderUtil() {
    }

    public static <T extends Enum<T>> List<SortOrder<T>> toSortOrderList(Function<String, T> valueOf, List<QuerySortOrder> querySortOrders) {
        return querySortOrders.stream()
                .map(querySortOrder -> new SortOrder<>(valueOf.apply(querySortOrder.getSorted()), querySortOrder.getDirection()))
                .toList();
    }
}
