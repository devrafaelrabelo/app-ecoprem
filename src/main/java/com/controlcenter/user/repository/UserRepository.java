package com.controlcenter.user.repository;

import com.controlcenter.entity.common.Position;
import com.controlcenter.entity.user.User;
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
    boolean existsByCpf(String cpf);
    boolean existsByPosition(Position position);


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

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.roles r
    LEFT JOIN FETCH r.permissions
    LEFT JOIN FETCH u.departments d
    LEFT JOIN FETCH u.functions f
    LEFT JOIN FETCH u.position
    LEFT JOIN FETCH u.status
    LEFT JOIN FETCH u.userGroups
    LEFT JOIN FETCH u.userPermissions up
    LEFT JOIN FETCH up.permission
    WHERE u.id = :id
""")
    Optional<User> findDetailedById(@Param("id") UUID id);

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

    @EntityGraph(attributePaths = {"roles", "status"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> fetchUserWithRolesAndStatus(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"roles", "status"})
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> fetchUserWithRolesAndStatusUsername(@Param("username") String username);


    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.userPermissions up
    LEFT JOIN FETCH u.roles r
    LEFT JOIN FETCH r.permissions
    WHERE u.id = :id
""")
    Optional<User> findWithPermissions(UUID id);

    @Query("""
    SELECT u FROM User u
    JOIN FETCH u.status
    LEFT JOIN FETCH u.roles
    WHERE u.email = :email
""")
    Optional<User> findByEmailForLogin(@Param("email") String email);


    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.roles r
    LEFT JOIN FETCH r.permissions
    WHERE u.id = :id
""")
    Optional<User> findByIdWithRolesAndPermissions(@Param("id") UUID id);



}
