package com.ecoprem.auth.service;

import com.ecoprem.entity.BackupCode;
import com.ecoprem.entity.User;
import com.ecoprem.auth.repository.BackupCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BackupCodeService {

    private final BackupCodeRepository backupCodeRepository;

    public List<String> generateBackupCodes(User user, int quantity) {
        // Remove códigos antigos
        List<BackupCode> oldCodes = backupCodeRepository.findByUser(user);
        backupCodeRepository.deleteAll(oldCodes);

        List<String> codes = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < quantity; i++) {
            byte[] buffer = new byte[6];  // 6 bytes ~ 8 chars base32-like
            random.nextBytes(buffer);
            String code = Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);

            BackupCode backupCode = new BackupCode();
            backupCode.setId(UUID.randomUUID());
            backupCode.setUser(user);
            backupCode.setCode(code);
            backupCode.setCreatedAt(LocalDateTime.now());
            backupCodeRepository.save(backupCode);

            codes.add(code);
        }
        return codes;
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
        List<BackupCode> oldCodes = backupCodeRepository.findByUser(user);
        backupCodeRepository.deleteAll(oldCodes);

        // Reutiliza o método de geração já existente
        return generateBackupCodes(user, quantity);
    }

    public void deleteAllBackupCodes(User user) {
        List<BackupCode> codes = backupCodeRepository.findByUser(user);
        backupCodeRepository.deleteAll(codes);
    }

}
