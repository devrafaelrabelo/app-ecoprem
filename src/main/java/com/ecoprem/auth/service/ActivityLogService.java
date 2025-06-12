package com.ecoprem.auth.service;

import com.ecoprem.auth.dto.ActivityLogResponse;
import com.ecoprem.entity.auth.ActivityLog;
import com.ecoprem.entity.user.User;
import com.ecoprem.auth.repository.ActivityLogRepository;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final LoginMetadataExtractor metadataExtractor;

    public void logActivity(User user, String activity, HttpServletRequest request) {
        String ip = metadataExtractor.getClientIp(request);
        String location = metadataExtractor.getLocation(ip);

        ActivityLog newlog = new ActivityLog();
        newlog.setId(UUID.randomUUID());
        newlog.setUser(user);
        newlog.setActivity(activity);
        newlog.setActivityDate(LocalDateTime.now());
        newlog.setIpAddress(ip);
        newlog.setLocation(location);

        activityLogRepository.save(newlog);
        log.info("📝 Atividade registrada: {} por {} em {}", activity, user.getUsername(), location);
    }

    public void logAdminAction(User adminUser, String action, User targetUser) {
        ActivityLog newlog = new ActivityLog();
        newlog.setId(UUID.randomUUID());
        newlog.setUser(adminUser);
        newlog.setActivity(action);
        newlog.setTargetUser(targetUser);
        newlog.setActivityDate(LocalDateTime.now());

        activityLogRepository.save(newlog);
        log.info("👮 Ação administrativa: {} por {} em {}", action, adminUser.getUsername(), targetUser.getUsername());
    }

    public List<ActivityLogResponse> getUserActivityLogs(User user) {
        return activityLogRepository.findByUserIdOrderByActivityDateDesc(user.getId()).stream()
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
