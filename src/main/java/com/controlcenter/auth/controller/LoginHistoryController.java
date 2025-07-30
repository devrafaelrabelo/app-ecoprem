package com.controlcenter.auth.controller;

import com.controlcenter.auth.dto.LoginHistoryResponse;
import com.controlcenter.entity.auth.LoginHistory;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/history")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryRepository loginHistoryRepository;

    @GetMapping
    public List<LoginHistoryResponse> getHistory(@AuthenticationPrincipal User user) {
        List<LoginHistory> histories = loginHistoryRepository.findAll()
                .stream()
                .filter(h -> h.getUser() != null && h.getUser().getId().equals(user.getId()))
                .toList();

        return histories.stream().map(h -> {
            LoginHistoryResponse dto = new LoginHistoryResponse();
            dto.setId(h.getId());
            dto.setLoginDate(h.getLoginDate());
            dto.setIpAddress(h.getIpAddress());
            dto.setLocation(h.getLocation()); // null por enquanto
            dto.setDevice(h.getDevice());
            dto.setBrowser(h.getBrowser());
            dto.setOperatingSystem(h.getOperatingSystem());
            dto.setSuccess(h.isSuccess());
            return dto;
        }).collect(Collectors.toList());
    }
}
