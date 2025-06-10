package com.ecoprem.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
@Schema(description = "Dados necessários para o registro de um novo usuário no sistema")
public class RegisterRequest {

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 4, message = "Username deve ter no mínimo 4 caracteres")
    @Schema(description = "Nome de usuário único", example = "joao.pedro")
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Schema(description = "E-mail institucional ou pessoal", example = "joao@empresa.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Schema(description = "Senha segura do usuário", example = "Admin@1234")
    private String password;

    @NotBlank(message = "Primeiro nome é obrigatório")
    @Schema(description = "Primeiro nome do usuário", example = "João")
    private String firstName;

    @NotBlank(message = "Último nome é obrigatório")
    @Schema(description = "Sobrenome do usuário", example = "Pedro")
    private String lastName;

    @Schema(description = "Nome completo (pode ser preenchido ou montado automaticamente)", example = "João Pedro")
    private String fullName;

    @Schema(description = "Nome social (opcional)", example = "João P.")
    private String socialName;

    @NotNull(message = "Lista de roles é obrigatória")
    @Size(min = 1, message = "Informe pelo menos um papel (role)")
    @Schema(description = "Lista de papéis (roles) atribuídos ao usuário", example = "[\"11111111-1111-1111-1111-111111111111\"]")
    private List<String> roles;

    @Schema(description = "Lista de IDs dos departamentos (setores)", example = "[\"uuid-depart-1\", \"uuid-depart-2\"]")
    private List<String> departments;

    @Schema(description = "Lista de IDs dos grupos colaborativos", example = "[\"uuid-group-1\"]")
    private List<String> userGroups;

    @Schema(description = "ID do nível de acesso (opcional)", example = "uuid-access-level")
    private String accessLevelId;
}
