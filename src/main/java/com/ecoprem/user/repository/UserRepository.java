package com.ecoprem.user.repository;

import com.ecoprem.entity.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("""
    SELECT u FROM User u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions
        LEFT JOIN FETCH u.status
        WHERE u.email = :email
    """)
    Optional<User> findByEmailWithStatusAndRoles(@Param("email") String email);
    @EntityGraph(attributePaths = {
            "roles", "departments", "userGroups", "position", "functions", "userPermissions.permission"
    })
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {
            "roles",
            "departments",
            "userGroups",
            "position",
            "functions",
            "userPermissions.permission"
    })
    Optional<User> findDetailedById(UUID id);

    @EntityGraph(attributePaths = {
            "roles",
            "departments",
            "userGroups",
            "position",
            "functions",
            "userPermissions.permission",
            "currentCorporatePhones",
            "currentInternalExtensions"
    })
    Optional<User> findWithContactDetailsById(UUID id);

    @EntityGraph(attributePaths = {
            "roles",
            "departments",
            "functions",
            "position",
            "status",
            "currentCorporatePhones",
            "currentInternalExtensions",
            "allocationHistories.company",
            "personalPhoneNumbers",
            "status"
    })
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsernameFetchAll(@Param("username") String username);
}
