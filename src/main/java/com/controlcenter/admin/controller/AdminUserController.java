package com.controlcenter.admin.controller;

import com.controlcenter.admin.dto.RegisterRequest;
import com.controlcenter.admin.dto.RegisterUser;
import com.controlcenter.entity.user.User;
import com.controlcenter.admin.service.AdminUserService;
import com.controlcenter.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

//    @GetMapping
//    @PreAuthorize("hasAuthority('user:read')")
//    public ResponseEntity<List<UserDTO>> getAllUsers() {
//        return ResponseEntity.ok(adminUserService.findAll());
//    }


    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<Page<UserListDTO>> searchUsers(
            @RequestParam(required = false) String nameOrEmail,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String preferredLanguage,
            @RequestParam(required = false) String interfaceTheme,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(required = false) Boolean emailVerified,
            @RequestParam(required = false) Boolean twoFactorEnabled,
            @RequestParam(required = false) Boolean firstLogin,
            @RequestParam(required = false) Boolean passwordCompromised,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastLoginFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lastLoginTo,
            @PageableDefault(page = 0, size = 20, sort = "fullName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        AdminUserSearchParams params = new AdminUserSearchParams();
        params.setNameOrEmail(nameOrEmail);
        params.setCpf(cpf);
        params.setStatus(status);
        params.setRole(role);
        params.setDepartment(department);
        params.setPosition(position);
        params.setPreferredLanguage(preferredLanguage);
        params.setInterfaceTheme(interfaceTheme);
        params.setLocked(locked);
        params.setEmailVerified(emailVerified);
        params.setTwoFactorEnabled(twoFactorEnabled);
        params.setFirstLogin(firstLogin);
        params.setPasswordCompromised(passwordCompromised);
        params.setCreatedFrom(createdFrom);
        params.setCreatedTo(createdTo);
        params.setLastLoginFrom(lastLoginFrom);
        params.setLastLoginTo(lastLoginTo);

        Page<UserListDTO> result = adminUserService.searchUsers(params, pageable);
        return ResponseEntity.ok(result);
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
    public ResponseEntity<?> createUser(@AuthenticationPrincipal User adminUser, @RequestBody RegisterUser request) {
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserDetailsDTO> getUserById(@PathVariable UUID id) {
        UserDetailsDTO user = adminUserService.getUserById(id);
        return ResponseEntity.ok(user);
    }

//    @PutMapping("/api/admin/users/{id}")
//    @PreAuthorize("hasAuthority('user:update')")
//    public ResponseEntity<Void> updateUser(
//            @PathVariable UUID id,
//            @Valid @RequestBody EditUserDTO dto
//    ) {
//        adminUserService.updateUser(id, dto);
//        return ResponseEntity.noContent().build();
//    }


}
