package com.example.whereabouts.humanresources.internal;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.humanresources.Employee;
import com.example.whereabouts.humanresources.EmployeeData;
import com.example.whereabouts.humanresources.EmployeeId;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface EmployeeRepository extends Repository {

    Optional<Employee> findById(EmployeeId id);

    EmployeeId insert(EmployeeData data);

    Employee update(Employee employee);
}
