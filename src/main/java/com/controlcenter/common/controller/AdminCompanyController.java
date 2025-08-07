package com.controlcenter.common.controller;

import com.controlcenter.common.dto.CompanyDTO;
import com.controlcenter.common.dto.CreateCompanyDTO;
import com.controlcenter.common.dto.UpdateCompanyDTO;
import com.controlcenter.common.service.AdminCompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
public class AdminCompanyController {

    private final AdminCompanyService adminCompanyService;

    @PreAuthorize("hasAuthority('company:read')")
    @GetMapping
    @Operation(summary = "Listar todas as empresas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de empresas retornada com sucesso")
    })
    public ResponseEntity<List<CompanyDTO>> listAll() {
        return ResponseEntity.ok(adminCompanyService.findAll());
    }

    @PreAuthorize("hasAuthority('company:read')")
    @GetMapping("/{id}")
    @Operation(summary = "Buscar empresa por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<CompanyDTO> getById(
            @Parameter(description = "ID da empresa", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(adminCompanyService.findById(id));
    }

    @PreAuthorize("hasAuthority('company:create')")
    @PostMapping
    @Operation(
            summary = "Criar nova empresa",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação da empresa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateCompanyDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "name": "BEMPRESA LTDA S/A",
                      "cnpj": "12.345.678/0001-99",
                      "legalName": "Empresa Tecnologia S/A",
                      "active": true,
                      "address": {
                        "street": "Av. Paulista",
                        "number": "1000",
                        "complement": "10º andar",
                        "neighborhood": "Bela Vista",
                        "city": "São Paulo",
                        "state": "SP",
                        "country": "Brasil",
                        "postalCode": "01310-100"
                      }
                    }
                """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa criada com sucesso (UUID retornado)")
    })
    public ResponseEntity<UUID> create(@Valid @RequestBody CreateCompanyDTO dto) {
        UUID id = adminCompanyService.create(dto);
        return ResponseEntity.ok(id);
    }

    @PreAuthorize("hasAuthority('company:update')")
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar dados de uma empresa",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados da empresa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateCompanyDTO.class),
                            examples = @ExampleObject(value = """
                    {
                      "name": "EMPRESA LTDA.",
                      "cnpj": "12.345.678/0001-99",
                      "legalName": "Empresa Soluções Integradas",
                      "active": false,
                      "address": {
                        "street": "Rua da Tecnologia",
                        "number": "250",
                        "complement": "Bloco B",
                        "neighborhood": "Centro",
                        "city": "Belo Horizonte",
                        "state": "MG",
                        "country": "Brasil",
                        "postalCode": "30123-000"
                      }
                    }
                """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empresa atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<Void> update(
            @Parameter(description = "ID da empresa a ser atualizada", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyDTO dto) {
        adminCompanyService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('company:delete')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar empresa por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empresa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da empresa a ser excluída", required = true)
            @PathVariable UUID id) {
        adminCompanyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}