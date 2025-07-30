package com.controlcenter.auth.controller;

import com.controlcenter.auth.dto.ActivityLogResponse;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.service.ActivityLogService;
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
