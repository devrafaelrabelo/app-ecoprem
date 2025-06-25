package com.ecoprem.user.repository;

import com.ecoprem.entity.user.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRequestRepository extends JpaRepository<UserRequest, UUID> {
    Optional<UserRequest> findByCpf(String cpf);
}