package com.ecoprem.user.service;

import com.ecoprem.user.dto.UserRequestDTO;
import com.ecoprem.user.dto.UserRequestListDTO;
import com.ecoprem.entity.user.UserRequest;
import com.ecoprem.enums.UserRequestStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRequestService {

    private final com.ecoprem.user.repository.UserRequestRepository userRequestRepository;

    @Transactional
    public void createUserRequest(UserRequestDTO dto) {
        userRequestRepository.findByCpf(dto.getCpf()).ifPresent(r -> {
            throw new IllegalArgumentException("Já existe uma solicitação com esse CPF.");
        });

        Pair<String, String> nameParts = splitFullName(dto.getFullName());

        UserRequest request = UserRequest.builder()
                .cpf(dto.getCpf())
                .birthDate(dto.getBirthDate())
                .firstName(nameParts.getLeft())
                .lastName(nameParts.getRight())
                .phone(dto.getPhone())
                .supervisorId(UUID.fromString(dto.getSupervisorId()))
                .leaderId(UUID.fromString(dto.getLeaderId()))
                .cep(dto.getCep())
                .street(dto.getStreet())
                .number(dto.getNumber())
                .complement(dto.getComplement())
                .city(dto.getCity())
                .state(dto.getState())
                .status(UserRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        userRequestRepository.save(request);
    }

    public List<UserRequestListDTO> listAllRequests() {
        return userRequestRepository.findAll().stream()
                .map(req -> UserRequestListDTO.builder()
                        .id(req.getId())
                        .cpf(req.getCpf())
                        .firstName(req.getFirstName())
                        .lastName(req.getLastName())
                        .phone(req.getPhone())
                        .status(req.getStatus())
                        .requestedAt(req.getRequestedAt())
                        .build())
                .toList();
    }

    private Pair<String, String> splitFullName(String fullName) {
        String[] parts = fullName.trim().split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";
        return Pair.of(firstName, lastName);
    }

}
