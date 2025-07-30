package com.controlcenter.entity.resource;

import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "allocation_history", schema = "resource")
public class AllocationHistory {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "resource_type", nullable = false)
    private String resourceType; // ex: "CORPORATE_PHONE", "EXTENSION", "EMPLOYMENT"

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne
    @JoinColumn(name = "registered_by")
    private User registeredBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}