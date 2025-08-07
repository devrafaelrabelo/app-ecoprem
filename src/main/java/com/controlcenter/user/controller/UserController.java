package com.controlcenter.user.controller;

import com.controlcenter.exceptions.exception.InvalidTokenException;
import com.controlcenter.user.dto.UserBasicDTO;
import com.controlcenter.auth.dto.UserProfileDTO;
import com.controlcenter.user.dto.ProfileDTO;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.user.service.UserService;
import com.controlcenter.entity.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Obter dados do usuário autenticado",
            description = "Retorna os dados do usuário autenticado com base no token de sessão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        try {
            UserBasicDTO profile = userService.getCurrentUserBasic(user);
            return ResponseEntity.ok(Map.of("success", true, "data", profile));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Obter perfil completo do usuário autenticado",
            description = "Retorna todas as informações de perfil do usuário autenticado, incluindo empresa, departamentos, ramais e recursos alocados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal User user) {
        try {
            ProfileDTO dto = userService.getCurrentUserProfile(user);
            return ResponseEntity.ok(Map.of("success", true, "data", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
