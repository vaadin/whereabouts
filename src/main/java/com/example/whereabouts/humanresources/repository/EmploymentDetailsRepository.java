package com.example.whereabouts.humanresources.repository;

import com.example.whereabouts.common.Repository;
import com.example.whereabouts.humanresources.EmployeeId;
import com.example.whereabouts.humanresources.EmploymentDetails;
import com.example.whereabouts.humanresources.EmploymentDetailsData;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;

/**
 * @see "Design decision: DD005-20251024-records-as-entities.md"
 * @see "Design Decision: DD009-20251029-jooq-user-types.md"
 */
@NullMarked
public interface EmploymentDetailsRepository extends Repository {

    Optional<EmploymentDetails> findById(EmployeeId id);

    EmploymentDetails insert(EmployeeId id, EmploymentDetailsData data);

    EmploymentDetails update(EmploymentDetails employee);
}
