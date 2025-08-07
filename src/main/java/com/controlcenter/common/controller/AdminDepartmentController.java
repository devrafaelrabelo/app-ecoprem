package com.controlcenter.common.controller;

import com.controlcenter.common.dto.DepartmentCreateDTO;
import com.controlcenter.common.dto.DepartmentDTO;
import com.controlcenter.common.dto.DepartmentManagerAssignDTO;
import com.controlcenter.common.dto.DepartmentUpdateDTO;
import com.controlcenter.common.service.AdminDepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
@Tag(name = "Admin - Departments", description = "Operações administrativas sobre departamentos")
public class AdminDepartmentController {

    private final AdminDepartmentService adminDepartmentService;

    @PreAuthorize("hasAuthority('department:read')")
    @GetMapping
    @Operation(summary = "Listar todos os departamentos")
    @ApiResponse(responseCode = "200", description = "Departamentos listados com sucesso")
    public ResponseEntity<List<DepartmentDTO>> listAll() {
        return ResponseEntity.ok(adminDepartmentService.findAll());
    }

    @PreAuthorize("hasAuthority('department:read')")
    @GetMapping("/{id}")
    @Operation(summary = "Buscar departamento por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado")
    })
    public ResponseEntity<DepartmentDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminDepartmentService.findById(id));
    }

    @PreAuthorize("hasAuthority('department:create')")
    @PostMapping
    @Operation(
            summary = "Criar um novo departamento",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do novo departamento",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DepartmentCreateDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "name": "Departamento de TI",
                              "description": "Responsável pela infraestrutura e sistemas"
                            }
                            """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<DepartmentDTO> create(@RequestBody DepartmentCreateDTO dto) {
        return ResponseEntity.ok(adminDepartmentService.create(dto));
    }

    @PreAuthorize("hasAuthority('department:update')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dados de um departamento",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados do departamento",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DepartmentUpdateDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "name": "Departamento de Operações",
                              "description": "Área responsável por processos internos"
                            }
                            """)
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "Departamento atualizado com sucesso")
    public ResponseEntity<DepartmentDTO> update(@PathVariable UUID id, @RequestBody DepartmentUpdateDTO dto) {
        return ResponseEntity.ok(adminDepartmentService.update(id, dto));
    }

    @PreAuthorize("hasAuthority('department:delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um departamento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Departamento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Departamento não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminDepartmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('department:read')")
    @GetMapping("/{id}/users")
    @Operation(summary = "Listar IDs dos usuários de um departamento")
    @ApiResponse(responseCode = "200", description = "Usuários listados com sucesso")
    public ResponseEntity<List<UUID>> listUsers(@PathVariable UUID id) {
        return ResponseEntity.ok(adminDepartmentService.getUserIds(id));
    }

    @PreAuthorize("hasAuthority('department:update')")
    @PutMapping("/{id}/users")
    @Operation(
            summary = "Atualizar usuários atribuídos ao departamento",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lista de IDs de usuários que devem pertencer ao departamento",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = List.class),
                            examples = @ExampleObject(value = """
                            [
                              "8d9e8d9f-92ab-a5b7-ff6c-889900112233",
                              "7a7e3db2-cb14-408b-998f-c10b32a40c92"
                            ]
                            """)
                    )
            )
    )
    @ApiResponse(responseCode = "204", description = "Usuários atualizados com sucesso")
    public ResponseEntity<Void> updateUsers(@PathVariable UUID id, @RequestBody List<UUID> userIds) {
        adminDepartmentService.updateUsers(id, userIds);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('department:assignManager')")
    @PutMapping("/{id}/assign-manager")
    @Operation(
            summary = "Atribuir ou remover o gerente de um departamento",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "ID do novo gerente (ou null para remover)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DepartmentManagerAssignDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "managerId": "8d9e8d9f-92ab-a5b7-ff6c-889900112233"
                            }
                            """)
                    )
            )
    )
    @ApiResponse(responseCode = "204", description = "Gerente atribuído com sucesso")
    public ResponseEntity<Void> assignManager(@PathVariable UUID id, @RequestBody DepartmentManagerAssignDTO dto) {
        adminDepartmentService.assignManager(id, dto.getManagerId());
        return ResponseEntity.noContent().build();
    }
}


