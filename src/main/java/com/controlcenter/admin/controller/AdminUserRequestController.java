package com.controlcenter.admin.controller;

import com.controlcenter.admin.service.AdminUserRequestService;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.dto.UserRequestDTO;
import com.controlcenter.user.dto.UserRequestDetailsDTO;
import com.controlcenter.user.dto.UserRequestListDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users/request")
@RequiredArgsConstructor
public class AdminUserRequestController {

    private final AdminUserRequestService adminUserRequestService;

    @Operation(summary = "Registrar uma solicitação de criação de usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já solicitado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('requestuser:create')")
    public ResponseEntity<Void> createUserRequest(
            @AuthenticationPrincipal User requester,
            @RequestBody @Valid UserRequestDTO dto) {
        adminUserRequestService.createUserRequest(requester, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter detalhes de uma solicitação específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    @PreAuthorize("hasAuthority('requestuser:read')")
    public ResponseEntity<UserRequestDetailsDTO> getRequestById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User requester) {
        return ResponseEntity.ok(adminUserRequestService.getRequestById(id));
    }

    @Operation(summary = "Listar todas as solicitações de criação de usuário")
    @ApiResponse(responseCode = "200", description = "Lista de solicitações retornada com sucesso")
    @GetMapping
    @PreAuthorize("hasAuthority('requestuser:read')")
    public ResponseEntity<List<UserRequestListDTO>> getAllRequests() {
        return ResponseEntity.ok(adminUserRequestService.listAllRequests());
    }
}
