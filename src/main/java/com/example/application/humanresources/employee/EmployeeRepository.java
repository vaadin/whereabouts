package com.example.application.humanresources.employee;

import com.example.application.common.CrudRepository;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EmployeeRepository extends CrudRepository<EmployeeId, Employee, EmployeeData> {


}
