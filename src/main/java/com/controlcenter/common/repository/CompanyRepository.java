package com.controlcenter.common.repository;

import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
    boolean existsByName(String name);
    boolean existsByCnpj(String cnpj);
    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.allocationHistories ah
    LEFT JOIN FETCH ah.company
    WHERE u.id = :id
""")
    Optional<User> findWithAllocationHistoryById(@Param("id") UUID id);
}
