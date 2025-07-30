package com.controlcenter.admin.repository;

import com.controlcenter.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = {
            "status", "roles", "departments", "position"
    })
    Page<User> findAll(Specification<User> spec, Pageable pageable);


    @EntityGraph(attributePaths = {
            "status",
            "roles",
            "departments",
            "functions",
            "position",
            "currentCorporatePhones",
            "currentInternalExtensions",
            "personalPhoneNumbers",      // @ElementCollection
            "userPermissions"            // @OneToMany
    })
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findFullById(@Param("id") UUID id);
}
