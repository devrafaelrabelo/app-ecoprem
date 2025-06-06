package com.ecoprem.admin.controller;

import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.entity.auth.User;
import com.ecoprem.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Admin - Usuários",
        description = "Endpoints administrativos para criação e gerenciamento de usuários do sistema."
)
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@AuthenticationPrincipal User adminUser, @RequestBody RegisterRequest request) {
        adminUserService.createUserByAdmin(request, adminUser);
        return ResponseEntity.ok("User created successfully.");
    }

//    @GetMapping("/list")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<AdminUserResponseDTO>> listUsers() {
//        return ResponseEntity.ok(adminUserService.getAllUsers());
//    }
}
