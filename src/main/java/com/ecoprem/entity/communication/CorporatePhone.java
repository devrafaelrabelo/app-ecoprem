package com.ecoprem.entity.communication;

import com.ecoprem.entity.common.Company;
import com.ecoprem.entity.user.User;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "corporate_phone")
public class CorporatePhone {
    @Id
    private UUID id;

    private String number;
    private String carrier;
    private String planType;
    private boolean active;

    @ManyToOne(optional = true)
    @JoinColumn(name = "current_user_id")
    private User currentUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;
}
