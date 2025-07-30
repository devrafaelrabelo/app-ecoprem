package com.controlcenter.resource.repository;

import com.controlcenter.entity.communication.CorporatePhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CorporatePhoneRepository extends JpaRepository<CorporatePhone, UUID> {

    @Modifying
    @Query(value = """
    INSERT INTO corporate_phone (
        id, number, carrier, plan_type, status, current_user_id, company_id, last_updated
    ) VALUES (
        :id, :number, CAST(:carrier AS carrier_type), CAST(:planType AS plan_type),
        CAST(:status AS phone_status), :currentUserId, :companyId, :lastUpdated
    )
    """, nativeQuery = true)
    void insertManual(
            @Param("id") UUID id,
            @Param("number") String number,
            @Param("carrier") String carrier,
            @Param("planType") String planType,
            @Param("status") String status,
            @Param("currentUserId") UUID currentUserId,
            @Param("companyId") UUID companyId,
            @Param("lastUpdated") LocalDateTime lastUpdated
    );

    @Modifying
    @Query(nativeQuery = true, value = """
    UPDATE corporate_phone
    SET
        number = :number,
        carrier = CAST(:carrier AS carrier_type),
        plan_type = CAST(:planType AS plan_type),
        status = CAST(:status AS phone_status),
        current_user_id = :currentUserId,
        company_id = :companyId,
        last_updated = :lastUpdated
    WHERE id = :id
""")
    int updateManual(@Param("id") UUID id,
                     @Param("number") String number,
                     @Param("carrier") String carrier,
                     @Param("planType") String planType,
                     @Param("status") String status,
                     @Param("currentUserId") UUID currentUserId,
                     @Param("companyId") UUID companyId,
                     @Param("lastUpdated") LocalDateTime lastUpdated);

    boolean existsByNumber(String number);
}
