package com.controlcenter.user.controller;

import com.controlcenter.entity.user.User;
import com.controlcenter.user.dto.DeleteUserRequestsDTO;
import com.controlcenter.user.dto.UserRequestDTO;
import com.controlcenter.user.dto.UserRequestDetailsDTO;
import com.controlcenter.user.dto.UserRequestListDTO;
import com.controlcenter.user.service.UserRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user/request")
@RequiredArgsConstructor
public class UserRequestController {

    private final UserRequestService userRequestService;

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
        userRequestService.createUserRequest(requester, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Listar solicitações feitas pelo usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de solicitações retornada com sucesso")
    @PreAuthorize("hasAuthority('requestuser:read')")
    public ResponseEntity<List<UserRequestListDTO>> getMyRequests(@AuthenticationPrincipal User requester) {
        return ResponseEntity.ok(userRequestService.listRequestsByRequester(requester));
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
        return ResponseEntity.ok(userRequestService.getRequestById(id, requester));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('requestuser:delete')")
    public ResponseEntity<Void> deleteOwnRequest(
            @PathVariable UUID id,
            @AuthenticationPrincipal User requester) {
        log.debug("Deleting user request with ID: {}", id);
        userRequestService.deleteOwnRequest(id, requester);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('requestuser:delete')")
    public ResponseEntity<Void> deleteOwnRequestsBatch(
            @AuthenticationPrincipal User requester,
            @RequestBody @Valid DeleteUserRequestsDTO dto) {

        log.debug("Deleting user requests with IDs: {}", dto.getRequestIds());
        userRequestService.deleteOwnRequests(dto.getRequestIds(), requester);
        return ResponseEntity.noContent().build();
    }
}
