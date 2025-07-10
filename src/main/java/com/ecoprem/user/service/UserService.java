package com.ecoprem.user.service;

import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.auth.dto.SessionUserResponse;
import com.ecoprem.entity.security.Permission;
import com.ecoprem.user.dto.UserBasicDTO;
import com.ecoprem.auth.exception.*;
import com.ecoprem.auth.repository.*;
import com.ecoprem.auth.service.ActiveSessionService;
import com.ecoprem.entity.common.Department;
import com.ecoprem.entity.security.Role;
import com.ecoprem.entity.user.User;
import com.ecoprem.entity.user.UserGroup;
import com.ecoprem.entity.security.UserStatus;
import com.ecoprem.user.dto.ProfileDTO;
import com.ecoprem.user.mapper.UserMapper;
import com.ecoprem.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.ecoprem.auth.util.ValidationUtil.isStrongPassword;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserGroupRepository userGroupRepository;
    private final ActiveSessionService activeSessionService;
    private final PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    public void register(RegisterRequest request) {
        validateEmailAndUsernameUniqueness(request);
        validatePasswordStrength(request.getPassword());

        List<Role> roles = resolveAndValidateRoles(request.getRoles());
        List<Department> departments = resolveDepartments(request.getDepartments()
                .stream().map(UUID::fromString).toList());
        List<UserGroup> userGroups = resolveUserGroups(request.getUserGroups()
                .stream().map(UUID::fromString).toList());


        User newUser = buildNewUser(request, roles, departments, userGroups);
        userRepository.save(newUser);
    }

    private User buildNewUser(RegisterRequest request,
                              List<Role> roles,
                              List<Department> departments,
                              List<UserGroup> userGroups) {

        String fullName = (request.getFullName() != null && !request.getFullName().isBlank())
                ? request.getFullName()
                : request.getFirstName() + " " + request.getLastName();

        return User.builder()
                .id(UUID.randomUUID())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .fullName(fullName)
                .socialName(request.getSocialName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(roles))
                .departments(new HashSet<>(departments))
                .userGroups(new HashSet<>(userGroups))
                .emailVerified(false)
                .firstLogin(true)
                .notificationsEnabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void validateEmailAndUsernameUniqueness(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("O e-mail já está em uso.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("O nome de usuário já está em uso.");
        }
    }

    private void validatePasswordStrength(String password) {
        if (!isStrongPassword(password)) {
            throw new PasswordTooWeakException("A senha deve ter no mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas e números.");
        }
    }

    private List<Role> resolveAndValidateRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new InvalidRequestException("É necessário informar pelo menos um papel (role).");
        }

        List<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new InvalidRequestException("Um ou mais papéis informados são inválidos.");
        }

        return roles;
    }

    private List<Department> resolveDepartments(List<UUID> departmentIds) {
        if (departmentIds == null || departmentIds.isEmpty()) return List.of();
        return departmentRepository.findAllById(departmentIds);
    }

    private List<UserGroup> resolveUserGroups(List<UUID> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) return List.of();
        return userGroupRepository.findAllById(groupIds);
    }

    public UserBasicDTO getCurrentUserBasic(User user) {
        User fullUser = userRepository.findDetailedById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return userMapper.toBasicDTO(fullUser);
    }

    public ProfileDTO getCurrentUserProfile(User user) {
        User fullUser =  userRepository.findByUsernameFetchAll(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return userMapper.toProfileDto(fullUser);
    }

    public SessionUserResponse toSessionUserResponse(User user) {
        List<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();

        return SessionUserResponse.builder()
                .valid(true)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .permissions(permissions)
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .build();
    }

    public void validateUserState(User user) {
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Você precisa verificar seu e-mail antes de fazer login.");
        }

        String status = Optional.ofNullable(user.getStatus())
                .map(UserStatus::getName)
                .map(String::toLowerCase)
                .orElse(null);

        if ("suspended".equals(status)) {
            throw new AccountSuspendedException("Sua conta foi suspensa. Entre em contato com o suporte.");
        }

        if ("deactivated".equals(status)) {
            throw new AccountNotActiveException("Sua conta está desativada.");
        }

        if (user.isAccountLocked()) {
            if (isUnlockTimeReached(user)) {
                unlockUser(user);
            } else {
                throw new AccountLockedException("Sua conta está temporariamente bloqueada. Tente novamente mais tarde.");
            }
        }
    }

    public boolean isUnlockTimeReached(User user) {
        return user.getAccountLockedAt() != null &&
                user.getAccountLockedAt().plusMinutes(15).isBefore(LocalDateTime.now());
    }

    public void unlockUser(User user) {
        user.setAccountLocked(false);
        user.setLoginAttempts(0);
        user.setAccountLockedAt(null);
        userRepository.save(user);
    }

    public String createUserSession(User user, HttpServletRequest request) {
        String sessionId = UUID.randomUUID().toString();
        activeSessionService.createSession(user, sessionId, request);
        return sessionId;
    }
}
