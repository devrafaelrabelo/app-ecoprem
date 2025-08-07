package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.AssignRoleRequest;
import com.controlcenter.entity.user.User;
import com.controlcenter.exceptions.exception.CustomAccessDeniedException;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.user.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Papéis de Usuário (Admin)", description = "Atribuição e revogação de papéis (roles) por administradores")
@RestController
@RequestMapping("/api/admin/user-roles")
@RequiredArgsConstructor
public class AdminUserRoleController {

    private final UserRepository userRepository;
    private final UserRoleService userRoleService;

    @PostMapping("/assign")
        @PreAuthorize("hasAuthority('role:assign')")
    @Operation(
            summary = "Atribuir papel a um usuário",
            description = "Associa um papel (role) a um usuário do sistema. Requer autoridade 'role:assign'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Papel atribuído com sucesso"),
            @ApiResponse(responseCode = "400", description = "Papel já atribuído ou dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrados")
    })
    public void assignRole(
            @RequestBody AssignRoleRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomAccessDeniedException("Admin user not found"));

        userRoleService.assignRoleToUser(
                request.userId(),
                request.roleId(),
                httpRequest,
                admin.getId(),
                admin.getUsername()
        );
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasAuthority('role:revoke')")
    @Operation(
            summary = "Revogar papel de um usuário",
            description = "Remove um papel (role) previamente atribuído a um usuário. Requer autoridade 'role:revoke'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Papel revogado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Papel não atribuído ao usuário"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou role não encontrados")
    })
    public void revokeRole(
            @RequestBody AssignRoleRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomAccessDeniedException("Admin user not found"));

        userRoleService.revokeRoleFromUser(
                request.userId(),
                request.roleId(),
                httpRequest,
                admin.getId(),
                admin.getUsername()
        );
    }
}
