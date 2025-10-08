package com.example.application.humanresources.internal;

import com.example.application.common.CrudRepository;
import com.example.application.humanresources.Employee;
import com.example.application.humanresources.EmployeeData;
import com.example.application.humanresources.EmployeeId;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EmployeeRepository extends CrudRepository<EmployeeId, Employee, EmployeeData> {

}
