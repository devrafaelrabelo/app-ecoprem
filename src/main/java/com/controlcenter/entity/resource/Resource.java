package com.controlcenter.entity.resource;

import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resources", schema = "resource")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private ResourceStatus status;

    private String description;

    // === Dados patrimoniais e técnicos ===

    @Column(nullable = false)
    private String assetTag;

    @Column(nullable = false)
    private String serialNumber;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    private LocalDate warrantyEndDate;

    // === Localização e setor atual ===

    private String location;

    private String responsibleSector;

    // === Relacionamentos com outras entidades ===

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "current_user_id")
    private User currentUser;

    @ManyToOne
    @JoinColumn(name = "resource_type_id", nullable = false)
    private ResourceType resourceType;

    @Builder.Default
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<AllocationHistory> allocationHistory = new ArrayList<>();

    // === Flags e status lógicos ===

    @Builder.Default
    @Column(nullable = false)
    private boolean availableForUse = true;

    // === Campos de auditoria ===

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
