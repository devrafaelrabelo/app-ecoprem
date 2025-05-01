package com.ecoprem.auth.service;

import com.ecoprem.auth.entity.ActivityLog;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.repository.ActivityLogRepository;
import com.ecoprem.auth.util.LoginMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
}
