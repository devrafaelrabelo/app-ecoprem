package com.controlcenter.auth.service;

import com.controlcenter.entity.auth.BackupCode;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.repository.BackupCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackupCodeService {

    private final BackupCodeRepository backupCodeRepository;

    public List<String> generateBackupCodes(User user, int quantity) {
        // 1. Remove todos os códigos antigos
        deleteCodesByUser(user);

        // 2. Gera os novos códigos
        List<String> generatedCodes = new ArrayList<>();
        List<BackupCode> entities = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            String code = generateRandomCode(); // ex: 8 caracteres alfanuméricos

            BackupCode backupCode = new BackupCode();
            backupCode.setId(UUID.randomUUID());
            backupCode.setUser(user);
            backupCode.setCode(code);
            backupCode.setUsed(false);
            backupCode.setCreatedAt(LocalDateTime.now());

            generatedCodes.add(code);
            entities.add(backupCode);
        }

        // 3. Salva os novos códigos no banco
        backupCodeRepository.saveAll(entities);

        // 4. Retorna os códigos (em texto) para exibição
        return generatedCodes;
    }

    public boolean validateBackupCode(User user, String code) {
        return backupCodeRepository.findByUserAndCodeAndUsedFalse(user, code)
                .map(backup -> {
                    backup.setUsed(true);
                    backup.setUsedAt(LocalDateTime.now());
                    backupCodeRepository.save(backup);
                    return true;
                }).orElse(false);
    }

    public List<BackupCode> getBackupCodes(User user) {
        return backupCodeRepository.findByUser(user);
    }

    public List<String> regenerateBackupCodes(User user, int quantity) {
        // Deleta todos os códigos antigos
        deleteCodesByUser(user);

        // Reutiliza o método de geração já existente
        return generateBackupCodes(user, quantity);
    }

    public void deleteAllBackupCodes(User user) {
        deleteCodesByUser(user);
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private void deleteCodesByUser(User user) {
        List<BackupCode> codes = backupCodeRepository.findByUser(user);
        if (!codes.isEmpty()) {
            backupCodeRepository.deleteAll(codes);
        }
    }

}
