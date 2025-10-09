package com.example.application.humanresources.internal;

import com.example.application.common.Repository;
import com.example.application.humanresources.Employee;
import com.example.application.humanresources.EmployeeData;
import com.example.application.humanresources.EmployeeId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface EmployeeRepository extends Repository {

    boolean isEmpty();

    Optional<Employee> findById(EmployeeId id);

    EmployeeId insert(EmployeeData data);

    Employee update(Employee employee);

    void deleteById(EmployeeId id);
}
