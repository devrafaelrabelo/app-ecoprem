package com.controlcenter.entity.communication;

import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.user.User;
import com.controlcenter.enums.CarrierType;
import com.controlcenter.enums.PhoneStatus;
import com.controlcenter.enums.PlanType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "corporate_phone", schema = "communication")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CorporatePhone {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", columnDefinition = "carrier_type")
    private CarrierType carrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", columnDefinition = "plan_type") // se for enum também
    private PlanType planType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "phone_status") // idem
    private PhoneStatus status;

    @Column(name = "whatsapp_block", nullable = false)
    private boolean whatsappBlock = false;

    @ManyToOne
    @JoinColumn(name = "current_user_id")
    private User currentUser;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}