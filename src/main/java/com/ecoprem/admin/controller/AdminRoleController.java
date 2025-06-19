package com.ecoprem.admin.controller;

import com.ecoprem.auth.dto.RoleResponse;
import com.ecoprem.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> listAll() {
        return ResponseEntity.ok(roleService.listAll());
    }
}