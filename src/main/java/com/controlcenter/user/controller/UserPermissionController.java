package com.controlcenter.user.controller;

import com.controlcenter.entity.user.User;
import com.controlcenter.user.dto.UserPermissionsResponse;
import com.controlcenter.user.service.UserPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    @GetMapping("/permissions")
    @Operation(summary = "Permissões e Menus", description = "Retorna as permissões efetivas e menus disponíveis para o usuário autenticado.")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<UserPermissionsResponse> getPermissions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userPermissionService.getPermissionsWithMenus(user));
    }
}
