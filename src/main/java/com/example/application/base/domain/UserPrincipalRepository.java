package com.example.application.base.domain;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@NullMarked
@Deprecated(forRemoval = true)
public interface UserPrincipalRepository extends JpaRepository<UserPrincipal, Long> {

    @Query("select up from UserPrincipal up where lower(up.user.username) = lower(:username)")
    Optional<UserPrincipal> findByUsername(String username);
}
