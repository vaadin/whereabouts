package com.example.application.humanresources.internal;

import com.example.application.common.Repository;
import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmploymentDetails;
import com.example.application.humanresources.EmploymentDetailsData;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

@NullMarked
public interface EmploymentDetailsRepository extends Repository {

    Optional<EmploymentDetails> findById(EmployeeId id);

    EmploymentDetails insert(EmployeeId id, EmploymentDetailsData data);

    EmploymentDetails update(EmploymentDetails employee);
}
