package com.example.application.humanresources;

import com.example.application.humanresources.internal.EmployeeQuery;
import com.example.application.humanresources.internal.EmployeeRepository;
import com.example.application.humanresources.internal.EmploymentDetailsRepository;
import com.example.application.security.AppRoles;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@PreAuthorize("hasRole('" + AppRoles.EMPLOYEE_READ + "')")
@NullMarked
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmploymentDetailsRepository employmentDetailsRepository;
    private final EmployeeQuery employeeQuery;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmploymentDetailsRepository employmentDetailsRepository,
                           EmployeeQuery employeeQuery) {
        this.employeeRepository = employeeRepository;
        this.employmentDetailsRepository = employmentDetailsRepository;
        this.employeeQuery = employeeQuery;
    }

    @Transactional(readOnly = true)
    public List<EmployeeReference> findEmployees(Pageable pageable, @Nullable String searchTerm) {
        return employeeQuery.findEmployees(pageable, searchTerm);
    }

    @Transactional(readOnly = true)
    public Set<EmployeeReference> getEmployeesById(Set<EmployeeId> ids) {
        return employeeQuery.findEmployeesByIds(ids);
    }

    @Transactional(readOnly = true)
    public Optional<EmployeeReference> getEmployeeById(EmployeeId id) {
        return employeeQuery.findEmployeesByIds(Set.of(id)).stream().findFirst();
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.EMPLOYEE_CREATE + "')")
    public EmployeeId insert(EmployeeData data) {
        return employeeRepository.insert(data);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findById(EmployeeId id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.EMPLOYEE_UPDATE + "')")
    public Employee update(Employee employee) {
        return employeeRepository.update(employee);
    }
}
