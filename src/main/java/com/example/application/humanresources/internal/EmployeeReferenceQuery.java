package com.example.application.humanresources.internal;

import com.example.application.humanresources.EmployeeFilter;
import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmployeeReference;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@NullMarked
public interface EmployeeReferenceQuery {

    List<EmployeeReference> findByFilter(Pageable pageable, EmployeeFilter filter);

    Set<EmployeeReference> findByIds(Set<EmployeeId> ids);
}
