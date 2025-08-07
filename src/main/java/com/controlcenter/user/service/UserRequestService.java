package com.controlcenter.user.service;

import com.controlcenter.exceptions.exception.ForbiddenOperationException;
import com.controlcenter.exceptions.exception.InvalidRequestStateException;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.dto.UserRequestDTO;
import com.controlcenter.user.dto.UserRequestDetailsDTO;
import com.controlcenter.user.dto.UserRequestListDTO;
import com.controlcenter.entity.user.UserRequest;
import com.controlcenter.enums.UserRequestStatus;
import com.controlcenter.user.repository.UserRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRequestService {

    private final UserRequestRepository userRequestRepository;

    @Transactional
    public void createUserRequest(User requester, UserRequestDTO dto) {
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
                .neighborhood(dto.getNeighborhood())
                .number(dto.getNumber())
                .complement(dto.getComplement())
                .city(dto.getCity())
                .state(dto.getState())
                .status(UserRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .requester(requester)  // ✅ novo campo aqui
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

    public UserRequestDetailsDTO getRequestById(UUID requestId, User requester) {
        UserRequest request = userRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada com ID: " + requestId));

        if (!request.getRequester().getId().equals(requester.getId())) {
            throw new SecurityException("Você não tem permissão para visualizar esta solicitação.");
        }

        System.out.println("Request found: " + request.getRequester().getUsername());

        return UserRequestDetailsDTO.builder()
                .id(request.getId())
                .cpf(request.getCpf())
                .fullName(request.getFirstName() + " " + request.getLastName())
                .birthDate(String.valueOf(request.getBirthDate()))
                .phone(request.getPhone())
                .supervisorId(request.getSupervisorId() != null ? request.getSupervisorId().toString() : null)
                .leaderId(request.getLeaderId() != null ? request.getLeaderId().toString() : null)
                .cep(request.getCep())
                .status(String.valueOf(request.getStatus()))
                .street(request.getStreet())
                .neighborhood(request.getNeighborhood())
                .number(request.getNumber())
                .complement(request.getComplement())
                .city(request.getCity())
                .state(request.getState())
                .requestedAt(String.valueOf(request.getRequestedAt()))
                .requestedName(request.getRequester().getUsername())
                .createdAt(String.valueOf(request.getCreatedAt()))
                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy().getUsername() : null)
                .build();

    }

    public List<UserRequestListDTO> listRequestsByRequester(User requester) {
        return userRequestRepository.findByRequester(requester)
                .stream()
                .map(this::toListDTO)
                .toList();
    }

    @Transactional
    public void deleteOwnRequest(UUID requestId, User requester) {
        UserRequest request = userRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada com ID: " + requestId));

        if (!request.getRequester().getId().equals(requester.getId())) {
            throw new ForbiddenOperationException("Você só pode excluir solicitações que você criou.");
        }

        if (request.getStatus() != UserRequestStatus.PENDING) {
            throw new InvalidRequestStateException("Apenas solicitações pendentes podem ser excluídas.");
        }

        userRequestRepository.delete(request);
    }

    @Transactional
    public void deleteOwnRequests(List<UUID> ids, User requester) {
        List<UserRequest> requests = userRequestRepository.findAllById(ids);

        for (UserRequest request : requests) {
            if (!request.getRequester().getId().equals(requester.getId())) {
                throw new ForbiddenOperationException("Você só pode excluir solicitações que você criou.");
            }

            if (request.getStatus() != UserRequestStatus.PENDING) {
                throw new InvalidRequestStateException("Todas as solicitações devem estar pendentes para serem excluídas.");
            }
        }

        userRequestRepository.deleteAll(requests);
    }

    private UserRequestListDTO toListDTO(UserRequest req) {
        return UserRequestListDTO.builder()
                .id(req.getId())
                .cpf(req.getCpf())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .phone(req.getPhone())
                .status(req.getStatus())
                .requestedAt(req.getRequestedAt())
                .build();
    }


    private Pair<String, String> splitFullName(String fullName) {
        String[] parts = fullName.trim().split(" ", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";
        return Pair.of(firstName, lastName);
    }

}
