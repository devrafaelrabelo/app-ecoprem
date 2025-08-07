package com.controlcenter.common.controller;
import com.controlcenter.common.dto.*;
import com.controlcenter.common.service.AdminFunctionService;
import com.controlcenter.common.service.FunctionAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/functions")
@RequiredArgsConstructor
@Tag(name = "Admin - Funções", description = "Gestão de funções no sistema")
public class AdminFunctionController {

    private final AdminFunctionService adminFunctionService;
    private final FunctionAssignmentService functionAssignmentService;

    @PreAuthorize("hasAuthority('function:read')")
    @Operation(summary = "Listar todas as funções")
    @ApiResponse(responseCode = "200", description = "Lista de funções retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<FunctionResponseDTO>> listAll() {
        return ResponseEntity.ok(adminFunctionService.findAll());
    }

    @PreAuthorize("hasAuthority('function:read')")
    @Operation(summary = "Buscar função por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Função encontrada"),
            @ApiResponse(responseCode = "404", description = "Função não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FunctionResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminFunctionService.findById(id));
    }

    @PreAuthorize("hasAuthority('function:create')")
    @Operation(summary = "Criar nova função")
    @ApiResponse(responseCode = "201", description = "Função criada com sucesso")
    @PostMapping
    public ResponseEntity<FunctionResponseDTO> create(@RequestBody @Valid FunctionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminFunctionService.create(dto));
    }

    @PreAuthorize("hasAuthority('function:update')")
    @Operation(summary = "Atualizar função")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Função atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Função não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FunctionResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid FunctionRequestDTO dto) {
        return ResponseEntity.ok(adminFunctionService.update(id, dto));
    }

    @PreAuthorize("hasAuthority('function:delete')")
    @Operation(summary = "Excluir função")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Função excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Função não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminFunctionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('function:assign')")
    @Operation(summary = "Atribuir função a um funcionário")
    @ApiResponse(responseCode = "200", description = "Função atribuída com sucesso")
    @PostMapping("/assign")
    public ResponseEntity<Void> assignFunction(@RequestBody AssignFunctionRequest request) {
        functionAssignmentService.assignFunctionToUser(request.getUserId(), request.getFunctionId());
        return ResponseEntity.ok().build();
    }
}

