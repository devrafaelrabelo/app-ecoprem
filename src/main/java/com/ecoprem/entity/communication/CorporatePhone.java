package com.ecoprem.entity.communication;

import com.ecoprem.entity.common.Company;
import com.ecoprem.entity.user.User;
import com.ecoprem.enums.CarrierType;
import com.ecoprem.enums.PhoneStatus;
import com.ecoprem.enums.PlanType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "corporate_phone")
@Data
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

    @ManyToOne
    @JoinColumn(name = "current_user_id")
    private User currentUser;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
}