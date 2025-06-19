package com.ecoprem.user.controller;

import com.ecoprem.entity.user.User;
import com.ecoprem.user.dto.UserPermissionsResponse;
import com.ecoprem.user.service.UserPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    @GetMapping("/permissions")
    @Operation(summary = "Permissões e Menus", description = "Retorna as permissões efetivas e menus disponíveis para o usuário autenticado.")
    public ResponseEntity<UserPermissionsResponse> getPermissions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userPermissionService.getPermissionsWithMenus(user));
    }
}
