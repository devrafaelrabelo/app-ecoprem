package com.ecoprem.admin.controller;

import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.auth.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        adminUserService.createUserByAdmin(request);
        return ResponseEntity.ok("User created successfully.");
    }
}
