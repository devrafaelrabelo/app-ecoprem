package com.ecoprem.admin.controller;

import com.ecoprem.admin.dto.AdminUserResponseDTO;
import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.entity.user.User;
import com.ecoprem.admin.service.AdminUserService;
import com.ecoprem.user.dto.CreateUserFromRequestDTO;
import com.ecoprem.user.dto.UserBasicDTO;
import com.ecoprem.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Admin - Usuários",
        description = "Endpoints administrativos para criação e gerenciamento de usuários do sistema."
)
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.findAll());
    }

    @Operation(
            summary = "Criar novo usuário",
            description = "Permite que um administrador crie manualmente um novo usuário no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário já existente"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<?> createUser(@AuthenticationPrincipal User adminUser, @RequestBody RegisterRequest request) {
        adminUserService.createUserByAdmin(request, adminUser);
        return ResponseEntity.ok("User created successfully.");
    }

    @PostMapping("/create-from-request/{id}")
    @Operation(
            summary = "Criar usuário com base em uma solicitação preenchida",
            description = "Cria um usuário a partir de uma UserRequest, utilizando dados preenchidos no frontend"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<String> createUserFromRequest(
            @AuthenticationPrincipal User adminUser,
            @PathVariable UUID id,
            @RequestBody @Valid CreateUserFromRequestDTO dto) {

        System.out.println("Creating user from request with ID: " + id);

        adminUserService.createUserFromRequest(id, dto, adminUser);
        return ResponseEntity.ok("Usuário criado com sucesso.");
    }


}
