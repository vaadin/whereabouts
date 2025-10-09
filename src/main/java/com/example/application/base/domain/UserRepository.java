package com.example.application.base.domain;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@NullMarked
@Deprecated(forRemoval = true)
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where lower(u.username) like lower(:searchTerm) or u.displayName like lower(:searchTerm) order by u.displayName")
    Slice<User> findBySearchTerm(String searchTerm, Pageable pageable);
}
