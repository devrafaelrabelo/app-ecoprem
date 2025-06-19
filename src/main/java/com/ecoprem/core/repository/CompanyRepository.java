package com.ecoprem.core.repository;

import com.ecoprem.entity.common.Company;
import com.ecoprem.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.allocationHistories ah
    LEFT JOIN FETCH ah.company
    WHERE u.id = :id
""")
    Optional<User> findWithAllocationHistoryById(@Param("id") UUID id);
}