package com.ecoprem.auth.repository;

import com.ecoprem.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("""
    SELECT u FROM User u
    JOIN FETCH u.status
    JOIN FETCH u.roles
    WHERE u.email = :email
""")
    Optional<User> findByEmailWithStatusAndRoles(@Param("email") String email);

}
