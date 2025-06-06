package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.ActivityLogResponse;
import com.ecoprem.entity.User;
import com.ecoprem.auth.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/activities")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    public List<ActivityLogResponse> getActivities(@AuthenticationPrincipal User user) {
        return activityLogService.getUserActivityLogs(user);
    }
}
