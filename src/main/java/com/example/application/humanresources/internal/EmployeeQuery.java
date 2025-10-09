package com.example.application.humanresources.internal;

import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmployeeReference;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@NullMarked
public interface EmployeeQuery {

    List<EmployeeReference> findEmployees(@Nullable String searchTerm, Pageable pageable);

    Optional<EmployeeReference> findEmployeeById(EmployeeId id);
}
