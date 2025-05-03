package com.ecoprem.admin.controller;

import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@AuthenticationPrincipal User adminUser, @RequestBody RegisterRequest request) {
        adminUserService.createUserByAdmin(request,adminUser);
        return ResponseEntity.ok("User created successfully.");
    }
}
