package com.ecoprem.admin.service;

import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.core.exception.UserRequestAlreadyProcessedException;
import com.ecoprem.core.exception.UserRequestNotFoundException;
import com.ecoprem.entity.security.Role;
import com.ecoprem.entity.user.User;
import com.ecoprem.auth.exception.EmailAlreadyExistsException;
import com.ecoprem.auth.exception.RoleNotFoundException;
import com.ecoprem.auth.exception.UsernameAlreadyExistsException;
import com.ecoprem.auth.repository.RoleRepository;
import com.ecoprem.entity.user.UserRequest;
import com.ecoprem.enums.UserRequestStatus;
import com.ecoprem.user.dto.CreateUserFromRequestDTO;
import com.ecoprem.user.repository.UserRepository;
import com.ecoprem.auth.service.ActivityLogService;
import com.ecoprem.user.repository.UserRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.ecoprem.auth.util.ValidationUtil.isStrongPassword;
import static com.ecoprem.auth.util.ValidationUtil.isValidEmail;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final UserRequestRepository userRequestRepository;

    public void createUserByAdmin(RegisterRequest request, User adminUser) {
        validateUserCreation(request);

        List<String> roleNames = request.getRoles(); // precisa ser List<String>
        List<Role> roles = roleRepository.findByNameIn(roleNames);

        if (roles.size() != roleNames.size()) {
            throw new RoleNotFoundException("Algumas roles informadas não foram encontradas: " + roleNames);
        }

        User newUser = buildUserFromRequest(request, roles);

        userRepository.save(newUser);

        activityLogService.logAdminAction(
                adminUser,
                "Created new user: " + newUser.getUsername() + " (" + newUser.getEmail() + ")",
                newUser
        );
    }

    @Transactional
    public void createUserFromRequest(UUID requestId, CreateUserFromRequestDTO dto, User adminUser) {
        UserRequest request = userRequestRepository.findById(requestId)
                .orElseThrow(() -> new UserRequestNotFoundException("Solicitação não encontrada"));

        if (request.getStatus() != UserRequestStatus.PENDING) {
            throw new UserRequestAlreadyProcessedException("Solicitação já processada.");
        }

        List<Role> roles = roleRepository.findByNameIn(dto.getRoles());
        if (roles.size() != dto.getRoles().size()) {
            throw new RoleNotFoundException("Algumas roles informadas não existem: " + dto.getRoles());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .fullName(request.getFirstName() + " " + request.getLastName())
                .cpf(request.getCpf())
                .birthDate(request.getBirthDate())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .managerId(request.getSupervisorId())
                .roles(new HashSet<>(roles))
                .build();

        // Associar departamentos, posição, grupos se necessário
        // Exemplo:
        // if (dto.getDepartmentIds() != null) { ... }

        userRepository.save(user);

        request.setStatus(UserRequestStatus.APPROVED);
        userRequestRepository.save(request);

        activityLogService.logAdminAction(
                adminUser,
                "Criou usuário a partir de solicitação: " + user.getUsername(),
                user
        );
    }

    /**
     *  Auxiliares
     */
    private void validateUserCreation(RegisterRequest request) {
        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("The email is already in use.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("The username is already in use.");
        }

        if (!isStrongPassword(request.getPassword())) {
            throw new IllegalArgumentException("Password must be at least 8 characters, include uppercase, lowercase letters and a number.");
        }
    }

    private User buildUserFromRequest(RegisterRequest request, List<Role> roles) {
        return User.builder()
                .id(UUID.randomUUID())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .fullName(request.getFullName() != null
                        ? request.getFullName()
                        : request.getFirstName() + " " + request.getLastName())
                .socialName(request.getSocialName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(roles))
                .emailVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
