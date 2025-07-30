package com.controlcenter.entity.user;

import com.controlcenter.enums.UserRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_request", schema = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private UUID supervisorId;

    @Column(nullable = false)
    private UUID leaderId;

    // Endereço
    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String number;

    private String complement;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false)
    private String state;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @Enumerated(EnumType.STRING)
    private UserRequestStatus status;

    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = true) // permitir null
    private User createdBy;

    @Column(name = "created_at", nullable = true) // permitir null
    private LocalDateTime createdAt;


}