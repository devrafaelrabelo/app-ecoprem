package com.controlcenter.admin.dto;

import com.controlcenter.common.dto.AddressDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUser {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Primeiro nome do usuário", example = "João")
    private String firstName;

    @NotBlank(message = "Sobrenome é obrigatório")
    @Schema(description = "Último nome do usuário", example = "Silva")
    private String lastName;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    private String fullName;

    @Schema(description = "Nome social do usuário", example = "Joana Silva")
    private String socialName;

    @NotBlank(message = "Nome de usuário é obrigatório")
    @Schema(description = "Username único para login", example = "joaosilva")
    private String username;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Schema(description = "E-mail institucional", example = "joao.silva@empresa.com")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Schema(description = "CPF do usuário (apenas números ou formatado)", example = "123.456.789-00")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Schema(description = "Data de nascimento do usuário", example = "1990-05-20")
    private LocalDate birthDate;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Schema(description = "Senha segura do usuário", example = "Admin@1234")
    private String password;

    @Schema(description = "Tema da interface do usuário", example = "light")
    private String interfaceTheme = "light";

    @Schema(description = "Fuso horário preferencial", example = "America/Sao_Paulo")
    private String timezone = "America/Sao_Paulo";

    @Schema(description = "Idioma preferido", example = "pt-BR")
    private String preferredLanguage = "pt-BR";

    @Schema(description = "Status do convite", example = "PENDING")
    private String invitationStatus;

    @Schema(description = "URL do avatar do usuário", example = "https://cdn.empresa.com/avatars/joao.png")
    private String avatar;

    @Schema(description = "Origem do cadastro (ex: admin, import, api)", example = "admin")
    private String origin;

    @Schema(description = "Versão da política de privacidade aceita", example = "v1.0")
    private String privacyPolicyVersion;

    @Schema(description = "Status de consentimento de cookies", example = "granted")
    private String cookieConsentStatus;

    @Schema(description = "ID do gerente direto do usuário")
    private UUID managerId;

    @Schema(description = "Habilitação do 2FA (autenticação em dois fatores)", example = "false")
    private Boolean twoFactorEnabled = false;

    @Schema(description = "Última localização conhecida do usuário", example = "São Paulo, SP")
    private String lastKnownLocation;

    @Schema(description = "Motivo da suspensão da conta", example = "Comportamento inadequado")
    private String accountSuspendedReason;

    @Schema(description = "E-mail verificado", example = "true")
    private Boolean emailVerified = false;

    @Schema(description = "Data de aceite dos termos de uso")
    private LocalDate termsAcceptedAt;

    // RELACIONAMENTOS (IDs)

    @Schema(description = "Lista de IDs das roles atribuídas ao usuário")
    private Set<UUID> roleIds;

    @Schema(description = "Lista de IDs dos departamentos do usuário")
    private Set<UUID> departmentIds;

    @Schema(description = "Lista de IDs dos grupos de usuário")
    private Set<UUID> groupIds;

    @Schema(description = "Lista de IDs das funções atribuídas ao usuário")
    private Set<UUID> functionIds;

    @Schema(description = "ID da posição ou cargo do usuário")
    private UUID positionId;

    @Schema(description = "ID do status da conta do usuário")
    private UUID statusId;

    @Schema(description = "Lista de telefones pessoais do usuário", example = "[\"11999998888\", \"11988887777\"]")
    private Set<String> personalPhoneNumbers;

    @Schema(description = "Endereço completo do usuário")
    private AddressDTO address;

    @Schema(description = "ID do usuário solicitante (requester)")
    private UUID requestedById;

    @Schema(description = "ID do administrador que criou o usuário")
    private UUID createdById;
}
