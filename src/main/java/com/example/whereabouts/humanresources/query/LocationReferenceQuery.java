package com.example.whereabouts.humanresources.query;

import com.example.whereabouts.humanresources.LocationId;
import com.example.whereabouts.humanresources.LocationReference;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@NullMarked
public interface LocationReferenceQuery {

    List<LocationReference> findBySearchTerm(Pageable pageable, @Nullable String searchTerm);

    Set<LocationReference> findByIds(Set<LocationId> ids);
}
