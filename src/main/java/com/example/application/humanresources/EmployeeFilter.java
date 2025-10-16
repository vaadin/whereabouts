package com.example.application.humanresources;

import com.example.application.common.SetUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

@NullMarked
public record EmployeeFilter(@Nullable String searchTerm, Set<EmploymentStatus> statuses,
                             Set<EmploymentType> types) {

    public EmployeeFilter(@Nullable String searchTerm, Set<EmploymentStatus> statuses, Set<EmploymentType> types) {
        this.searchTerm = searchTerm;
        this.statuses = Set.copyOf(statuses);
        this.types = Set.copyOf(types);
    }

    public EmployeeFilter withSearchTerm(@Nullable String searchTerm) {
        return new EmployeeFilter(searchTerm, statuses, types);
    }

    public EmployeeFilter withStatus(EmploymentStatus status) {
        return new EmployeeFilter(searchTerm, SetUtil.add(statuses, status), types);
    }

    public EmployeeFilter withoutStatus(EmploymentStatus status) {
        return new EmployeeFilter(searchTerm, SetUtil.remove(statuses, status), types);
    }

    public EmployeeFilter withType(EmploymentType type) {
        return new EmployeeFilter(searchTerm, statuses, SetUtil.add(types, type));
    }

    public EmployeeFilter withoutType(EmploymentType type) {
        return new EmployeeFilter(searchTerm, statuses, SetUtil.remove(types, type));
    }

    public static EmployeeFilter empty() {
        return new EmployeeFilter(null, Collections.emptySet(), Collections.emptySet());
    }
}
