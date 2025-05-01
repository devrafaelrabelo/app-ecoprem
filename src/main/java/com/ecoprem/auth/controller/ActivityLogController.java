package com.ecoprem.auth.controller;

import com.ecoprem.auth.dto.ActivityLogResponse;
import com.ecoprem.auth.repository.ActivityLogRepository;
import com.ecoprem.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/activities")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;

    @GetMapping
    public List<ActivityLogResponse> getActivities(@AuthenticationPrincipal User user) {
        return activityLogRepository.findByUserId(user.getId()).stream().map(log -> {
            ActivityLogResponse dto = new ActivityLogResponse();
            dto.setId(log.getId());
            dto.setActivity(log.getActivity());
            dto.setActivityDate(log.getActivityDate());
            dto.setIpAddress(log.getIpAddress());
            dto.setLocation(log.getLocation());
            return dto;
        }).collect(Collectors.toList());
    }
}
