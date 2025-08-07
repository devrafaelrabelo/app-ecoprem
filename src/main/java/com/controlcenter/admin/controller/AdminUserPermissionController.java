package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.AssignPermissionRequest;
import com.controlcenter.admin.dto.BatchPermissionAssignmentRequest;
import com.controlcenter.auth.dto.UserPermissionDTO;
import com.controlcenter.entity.user.User;
import com.controlcenter.exceptions.exception.CustomAccessDeniedException;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.user.service.UserPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user-permissions")
@RequiredArgsConstructor
public class AdminUserPermissionController {

    private final UserPermissionService userPermissionService;
    private final UserRepository userRepository;


    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('permission:assign')")
    @Operation(
            summary = "Atribuir permissão a um usuário",
            description = "Associa uma permissão específica a um usuário, se ela ainda não tiver sido atribuída. Requer autoridade 'permission:assign'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão atribuída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Permissão já atribuída ou dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrados")
    })
    public void assignPermission(
            @RequestBody AssignPermissionRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        userPermissionService.assignPermission(
                new UserPermissionDTO(request.getUserId(), request.getPermissionId()),
                httpRequest,
                admin.getId(),
                admin.getUsername()
        );
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasAuthority('permission:revoke')")
    @Operation(
            summary = "Revogar permissão de um usuário",
            description = "Remove uma permissão previamente atribuída a um usuário. Requer autoridade 'permission:revoke'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão revogada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Permissão não atribuída ao usuário"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou permissão não encontrados")
    })
    public void revokePermission(
            @RequestBody AssignPermissionRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomAccessDeniedException("Admin user not found"));

        userPermissionService.revokePermission(
                new UserPermissionDTO(request.getUserId(), request.getPermissionId()),
                httpRequest,
                admin.getId(),
                admin.getUsername()
        );
    }

    @PostMapping("/assign-batch")
    @PreAuthorize("hasAuthority('permission:assign')")
    @Operation(summary = "Atribuir múltiplas permissões a um usuário")
    public void assignPermissionsInBatch(
            @RequestBody BatchPermissionAssignmentRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomAccessDeniedException("Admin user not found"));

        userPermissionService.assignPermissionsInBatch(
                request.userId(),
                request.permissionIds(),
                httpRequest,
                admin.getId(),
                admin.getUsername()
        );
    }

    @PostMapping("/revoke-batch")
    @PreAuthorize("hasAuthority('permission:revoke')")
    @Operation(summary = "Revogar múltiplas permissões de um usuário")
    public void revokePermissionsInBatch(
            @RequestBody BatchPermissionAssignmentRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        userPermissionService.revokePermissionsInBatch(
                request.userId(),
                request.permissionIds(),
                httpRequest,
                admin.getId(),
                admin.getUsername()
        );
    }
}
