package com.ecoprem.user.controller;

import com.ecoprem.user.dto.UserRequestDTO;
import com.ecoprem.user.dto.UserRequestListDTO;
import com.ecoprem.user.service.UserRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/request")
@RequiredArgsConstructor
public class UserRequestController {

    private final UserRequestService service;

    @Operation(summary = "Registrar uma solicitação de criação de usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou CPF já solicitado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('requestuser:create')")
    public ResponseEntity<?> createUserRequest(@RequestBody @Valid UserRequestDTO dto) {
        service.createUserRequest(dto);
        return ResponseEntity.ok("Solicitação registrada com sucesso.");
    }

    @Operation(summary = "Listar todas as solicitações de criação de usuário")
    @ApiResponse(responseCode = "200", description = "Lista de solicitações retornada com sucesso")
    @GetMapping
    @PreAuthorize("hasAuthority('requestuser:read')")
    public ResponseEntity<List<UserRequestListDTO>> getAllRequests() {
        return ResponseEntity.ok(service.listAllRequests());
    }
}
