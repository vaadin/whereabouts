package com.example.application.humanresources.internal;

import com.example.application.common.Repository;
import com.example.application.humanresources.EmployeeId;
import com.example.application.humanresources.EmploymentDetails;
import com.example.application.humanresources.EmploymentDetailsData;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EmploymentDetailsRepository extends
        Repository.WithUpdate<EmployeeId, EmploymentDetails, EmploymentDetailsData>,
        Repository.WithDelete<EmployeeId, EmploymentDetails, EmploymentDetailsData> {

    EmploymentDetails insert(EmployeeId id, EmploymentDetailsData data);
}
