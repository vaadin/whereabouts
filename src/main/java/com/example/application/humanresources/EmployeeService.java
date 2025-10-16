package com.example.application.humanresources;

import com.example.application.humanresources.internal.EmployeeReferenceQuery;
import com.example.application.humanresources.internal.EmployeeRepository;
import com.example.application.humanresources.internal.EmploymentDetailsRepository;
import com.example.application.security.AppRoles;
import org.jspecify.annotations.NullMarked;
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
    private final EmployeeReferenceQuery employeeReferenceQuery;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmploymentDetailsRepository employmentDetailsRepository,
                           EmployeeReferenceQuery employeeReferenceQuery) {
        this.employeeRepository = employeeRepository;
        this.employmentDetailsRepository = employmentDetailsRepository;
        this.employeeReferenceQuery = employeeReferenceQuery;
    }

    @Transactional(readOnly = true)
    public List<EmployeeReference> findReferencesByFilter(Pageable pageable, EmployeeFilter filter) {
        return employeeReferenceQuery.findByFilter(pageable, filter);
    }

    @Transactional(readOnly = true)
    public Set<EmployeeReference> findReferencesByIds(Set<EmployeeId> ids) {
        return employeeReferenceQuery.findByIds(ids);
    }

    @Transactional(readOnly = true)
    public Optional<EmployeeReference> findReferenceById(EmployeeId id) {
        return employeeReferenceQuery.findByIds(Set.of(id)).stream().findFirst();
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

    @Transactional(readOnly = true)
    public Optional<EmploymentDetails> findDetailsById(EmployeeId id) {
        return employmentDetailsRepository.findById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.EMPLOYEE_CREATE + "')")
    public EmploymentDetails insertDetails(EmployeeId id, EmploymentDetailsData data) {
        return employmentDetailsRepository.insert(id, data);
    }

    @Transactional
    @PreAuthorize("hasRole('" + AppRoles.EMPLOYEE_UPDATE + "')")
    public EmploymentDetails updateDetails(EmploymentDetails details) {
        return employmentDetailsRepository.update(details);
    }
}
