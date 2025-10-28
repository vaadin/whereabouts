package com.example.whereabouts.humanresources.query;

import com.example.whereabouts.humanresources.EmployeeFilter;
import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.humanresources.EmployeeReference;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@NullMarked
public interface EmployeeReferenceQuery {

    List<EmployeeReference> findByFilter(Pageable pageable, EmployeeFilter filter);

    Set<EmployeeReference> findByIds(Set<EmployeeId> ids);
}
