package com.ecoprem.auth.service;

import com.ecoprem.auth.dto.ActivityLogResponse;
import com.ecoprem.entity.ActivityLog;
import com.ecoprem.entity.User;
import com.ecoprem.auth.repository.ActivityLogRepository;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final LoginMetadataExtractor metadataExtractor;

    public void logActivity(User user, String activity, HttpServletRequest request) {
        String ip = metadataExtractor.getClientIp(request);
        String location = metadataExtractor.getLocation(ip);

        ActivityLog log = new ActivityLog();
        log.setId(UUID.randomUUID());
        log.setUser(user);
        log.setActivity(activity);
        log.setActivityDate(LocalDateTime.now());
        log.setIpAddress(ip);
        log.setLocation(location);

        activityLogRepository.save(log);
    }

    public void logAdminAction(User adminUser, String action, User targetUser) {
        ActivityLog log = new ActivityLog();
        log.setId(UUID.randomUUID());
        log.setUser(adminUser);  // quem executou a ação
        log.setActivity(action);   // descrição livre da ação, ex: "Created user: X"
        log.setTargetUser(targetUser);  // quem foi afetado (opcional)
        log.setActivityDate(LocalDateTime.now());

        // (opcional) você pode adicionar IP/metadata também
        activityLogRepository.save(log);
    }

    public List<ActivityLogResponse> getUserActivityLogs(User user) {
        return activityLogRepository.findByUserId(user.getId()).stream()
                .map(log -> ActivityLogResponse.builder()
                        .id(log.getId())
                        .activity(log.getActivity())
                        .activityDate(log.getActivityDate())
                        .ipAddress(log.getIpAddress())
                        .location(log.getLocation())
                        .build()
                ).toList();
    }

}
