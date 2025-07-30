package com.controlcenter.admin.service;

import com.controlcenter.admin.repository.AdminUserRepository;
import com.controlcenter.auth.dto.RegisterRequest;
import com.controlcenter.auth.exception.UserNotFoundException;
import com.controlcenter.core.exception.UserRequestAlreadyProcessedException;
import com.controlcenter.core.exception.UserRequestNotFoundException;
import com.controlcenter.entity.common.Company;
import com.controlcenter.entity.common.Department;
import com.controlcenter.entity.common.Position;
import com.controlcenter.entity.security.Role;
import com.controlcenter.entity.security.UserStatus;
import com.controlcenter.entity.user.User;
import com.controlcenter.auth.exception.EmailAlreadyExistsException;
import com.controlcenter.auth.exception.RoleNotFoundException;
import com.controlcenter.auth.exception.UsernameAlreadyExistsException;
import com.controlcenter.auth.repository.RoleRepository;
import com.controlcenter.entity.user.UserRequest;
import com.controlcenter.enums.UserRequestStatus;
import com.controlcenter.user.dto.*;
import com.controlcenter.user.mapper.UserMapper;
import com.controlcenter.user.repository.UserRepository;
import com.controlcenter.auth.service.ActivityLogService;
import com.controlcenter.user.repository.UserRequestRepository;
import com.controlcenter.user.spec.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.controlcenter.auth.util.ValidationUtil.isStrongPassword;
import static com.controlcenter.auth.util.ValidationUtil.isValidEmail;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final UserRequestRepository userRequestRepository;

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<UserListDTO> searchUsers(AdminUserSearchParams params, Pageable pageable) {
        Specification<User> spec = UserSpecification.build(params);
        return adminUserRepository.findAll(spec, pageable)
                .map(UserMapper::toListDTO);
    }

//    @Transactional
//    public void updateUser(UUID userId, EditUserDTO dto) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
//
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setFullName(dto.getFullName());
//        user.setSocialName(dto.getSocialName());
//        user.setEmail(dto.getEmail());
//        user.setCpf(dto.getCpf());
//        user.setBirthDate(dto.getBirthDate());
//        user.setPreferredLanguage(dto.getPreferredLanguage());
//        user.setInterfaceTheme(dto.getInterfaceTheme());
//        user.setNotificationsEnabled(dto.isNotificationsEnabled());
//
//        // Relacionamentos
//        if (dto.getPositionId() != null) {
//            Position position = positionRepository.findById(dto.getPositionId())
//                    .orElseThrow(() -> new NotFoundException("Cargo não encontrado"));
//            user.setPosition(position);
//        } else {
//            user.setPosition(null);
//        }
//
//        if (dto.getStatusId() != null) {
//            UserStatus status = userStatusRepository.findById(dto.getStatusId())
//                    .orElseThrow(() -> new NotFoundException("Status não encontrado"));
//            user.setStatus(status);
//        }
//
//        user.setRoles(new HashSet<>(roleRepository.findAllById(dto.getRoleIds())));
//        user.setDepartments(new HashSet<>(departmentRepository.findAllById(dto.getDepartmentIds())));
//        user.setFunctions(new HashSet<>(functionRepository.findAllById(dto.getFunctionIds())));
//        user.setCurrentCorporatePhones(new HashSet<>(corporatePhoneRepository.findAllById(dto.getCorporatePhoneIds())));
//        user.setCurrentInternalExtensions(new HashSet<>(internalExtensionRepository.findAllById(dto.getInternalExtensionIds())));
//
//        user.setUpdatedAt(LocalDateTime.now());
//
//        userRepository.save(user); // Opcional com @Transactional
//    }

    public UserDetailsDTO getUserById(UUID id) {
        User user = adminUserRepository.findFullById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return UserMapper.toDetailsDTO(user); // sem company
    }

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
                .requestedBy(request.getRequester()) // quem solicitou
                .createdBy(adminUser)
                .managerId(request.getSupervisorId())
                .roles(new HashSet<>(roles))
                .build();

        // Associar departamentos, posição, grupos se necessário
        // Exemplo:
        // if (dto.getDepartmentIds() != null) { ... }

        userRepository.save(user);

        request.setCreatedAt(LocalDateTime.now());
        request.setCreatedBy(adminUser);
        request.setStatus(UserRequestStatus.CREATED);
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
