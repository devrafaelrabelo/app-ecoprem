package com.controlcenter.common.service;

import com.controlcenter.common.repository.FunctionRepository;
import com.controlcenter.entity.audit.SecurityAuditEvent;
import com.controlcenter.entity.common.Function;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FunctionAssignmentService {

    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;

    public void assignFunctionsToUser(UUID userId, List<UUID> functionIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        List<Function> functions = functionRepository.findAllById(functionIds);

        if (functions.size() != functionIds.size()) {
            throw new IllegalArgumentException("Alguma função não foi encontrada.");
        }

        user.setFunctions(new HashSet<>(functions));
        userRepository.save(user);
    }

    @Transactional
    public void assignFunctionToUser(UUID userId, UUID functionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + userId));

        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new EntityNotFoundException("Função não encontrada: " + functionId));

        if (user.getFunctions().contains(function)) {
            throw new IllegalArgumentException("Usuário já possui essa função atribuída.");
        }

        user.getFunctions().add(function);
        userRepository.save(user);

//        auditLogService.registerAction("Função atribuída", "Função '" + function.getName() +
//                "' atribuída ao usuário '" + user.getUsername() + "'.", userId);
    }
}
