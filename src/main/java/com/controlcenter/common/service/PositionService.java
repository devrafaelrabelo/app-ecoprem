package com.controlcenter.common.service;



import com.controlcenter.common.dto.PositionRequestDTO;
import com.controlcenter.common.dto.PositionResponseDTO;
import com.controlcenter.common.repository.PositionRepository;
import com.controlcenter.entity.common.Position;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;

    public List<PositionResponseDTO> findAll() {
        return positionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PositionResponseDTO findById(UUID id) {
        return toDTO(positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cargo não encontrado")));
    }

    public List<PositionResponseDTO> findByFilters(String name, String description) {
        List<Position> results;

        if (name != null && description != null) {
            results = positionRepository.findByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCase(name, description);
        } else if (name != null) {
            results = positionRepository.findByNameContainingIgnoreCase(name);
        } else if (description != null) {
            results = positionRepository.findByDescriptionContainingIgnoreCase(description);
        } else {
            results = positionRepository.findAll();
        }

        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public PositionResponseDTO create(PositionRequestDTO dto) {
        if (positionRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Cargo já existe com o nome informado.");
        }

        Position position = new Position();
        position.setId(UUID.randomUUID());
        position.setName(dto.getName());
        position.setDescription(dto.getDescription());

        return toDTO(positionRepository.save(position));
    }

    private PositionResponseDTO toDTO(Position position) {
        PositionResponseDTO dto = new PositionResponseDTO();
        dto.setId(position.getId());
        dto.setName(position.getName());
        dto.setDescription(position.getDescription());
        return dto;
    }

    @Transactional
    public PositionResponseDTO update(UUID id, PositionRequestDTO dto) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado com ID: " + id));

        // Evita conflitos de nome duplicado
        if (positionRepository.existsByName(dto.getName()) &&
                !position.getName().equalsIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Já existe um cargo com o nome informado.");
        }

        position.setName(dto.getName());
        position.setDescription(dto.getDescription());

        return toDTO(positionRepository.save(position));
    }


    @Transactional
    public void delete(UUID id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado com ID: " + id));

        if (userRepository.existsByPosition(position)) {
            throw new IllegalStateException("Não é possível excluir o cargo. Ele está vinculado a um ou mais usuários.");
        }

        positionRepository.delete(position);
    }

    @Transactional
    public void assignPositionToUser(UUID userId, UUID positionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + userId));

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new EntityNotFoundException("Cargo não encontrado com ID: " + positionId));

        user.setPosition(position);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }
}