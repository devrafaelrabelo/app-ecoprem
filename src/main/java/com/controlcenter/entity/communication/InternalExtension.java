package com.controlcenter.entity.communication;

import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "internal_extension", schema = "communication")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InternalExtension {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String application;

    @ManyToOne(optional = true)
    @JoinColumn(name = "current_user_id", nullable = true)
    private User currentUser;

    @ManyToOne(optional = true)
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}