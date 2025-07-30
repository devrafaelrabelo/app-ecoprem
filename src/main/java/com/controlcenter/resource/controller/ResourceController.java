package com.controlcenter.resource.controller;

import com.controlcenter.resource.dto.ResourceCreateRequest;
import com.controlcenter.resource.dto.ResourceResponse;
import com.controlcenter.resource.dto.ResourceUpdateRequest;
import com.controlcenter.resource.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Recursos", description = "Gerenciamento de recursos corporativos (ativos)")
@RestController
@RequestMapping(value = "/api/resources", produces = "application/json")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @Operation(
            summary = "Listar recursos",
            description = "Retorna a lista paginada de recursos registrados no sistema. "
                    + "Permite filtro por status ou busca por texto (nome, patrimônio ou número de série)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de recursos paginada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido informado no filtro"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping
    public ResponseEntity<Page<ResourceResponse>> listAll(
            @Parameter(description = "Código do status do recurso (ex: DISPONIVEL, ALOCADO)")
            @RequestParam(required = false) String status,

            @Parameter(description = "Texto de busca: nome, número de patrimônio ou número de série")
            @RequestParam(required = false) String q,

            @Parameter(hidden = true) Pageable pageable
    ) {
        if (q != null && !q.isEmpty()) {
            return ResponseEntity.ok(resourceService.search(q, pageable));
        }
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(resourceService.listByStatus(status, pageable));
        }
        return ResponseEntity.ok(resourceService.listAll(pageable));
    }

    @Operation(
            summary = "Buscar recurso por ID",
            description = "Retorna os dados de um recurso específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de recursos paginada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido informado no filtro"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getById(
            @Parameter(description = "ID do recurso") @PathVariable UUID id
    ) {
        return ResponseEntity.ok(resourceService.getById(id));
    }

    @Operation(
            summary = "Criar novo recurso",
            description = "Registra um novo recurso (ativo corporativo) no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status, empresa, tipo ou usuário inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao criar recurso")
    })
    @PostMapping
    public ResponseEntity<ResourceResponse> create(
            @RequestBody ResourceCreateRequest request
    ) {
        ResourceResponse response = resourceService.createResource(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "Atualizar recurso",
            description = "Atualiza os dados de um recurso existente pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status, empresa, tipo ou usuário inválido"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao atualizar recurso")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> update(
            @Parameter(description = "ID do recurso a ser atualizado") @PathVariable UUID id,
            @RequestBody ResourceUpdateRequest request
    ) {
        return ResponseEntity.ok(resourceService.updateResource(id, request));
    }

    @Operation(
            summary = "Excluir recurso",
            description = "Remove um recurso do sistema pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recurso excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado para exclusão")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do recurso a ser excluído") @PathVariable UUID id
    ) {
        resourceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}