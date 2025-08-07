package com.controlcenter.admin.service;

import com.controlcenter.admin.dto.RegisterUser;
import com.controlcenter.admin.repository.AdminUserRepository;
import com.controlcenter.admin.dto.RegisterRequest;
import com.controlcenter.auth.repository.UserGroupRepository;
import com.controlcenter.auth.repository.UserStatusRepository;
import com.controlcenter.common.dto.AddressDTO;
import com.controlcenter.common.repository.DepartmentRepository;
import com.controlcenter.common.repository.FunctionRepository;
import com.controlcenter.common.repository.PositionRepository;
import com.controlcenter.entity.common.Address;
import com.controlcenter.exceptions.exception.EmailAlreadyExistsException;
import com.controlcenter.exceptions.exception.UserNotFoundException;
import com.controlcenter.exceptions.exception.UserRequestAlreadyProcessedException;
import com.controlcenter.exceptions.exception.UserRequestNotFoundException;
import com.controlcenter.entity.common.Department;
import com.controlcenter.entity.common.Function;
import com.controlcenter.entity.common.Position;
import com.controlcenter.entity.security.Role;
import com.controlcenter.entity.security.UserStatus;
import com.controlcenter.entity.user.User;
import com.controlcenter.exceptions.exception.RoleNotFoundException;
import com.controlcenter.exceptions.exception.UsernameAlreadyExistsException;
import com.controlcenter.auth.repository.RoleRepository;
import com.controlcenter.entity.user.UserGroup;
import com.controlcenter.entity.user.UserRequest;
import com.controlcenter.enums.UserRequestStatus;
import com.controlcenter.exceptions.exception.*;
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
import java.util.Set;
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
    private final DepartmentRepository departmentRepository;
    private final UserGroupRepository userGroupRepository;
    private final FunctionRepository functionRepository;
    private final PositionRepository positionRepository;
    private final UserStatusRepository userStatusRepository;

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

    public void createUserByAdmin(RegisterUser request, User adminUser) {
        validateUserCreation(request);

        // === Validações com exceções específicas ===

        Set<UUID> roleIds = request.getRoleIds();
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            Set<UUID> foundIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            roleIds.removeAll(foundIds);
            throw new RoleNotFoundException("Roles não encontradas: " + roleIds);
        }

        Set<UUID> deptIds = request.getDepartmentIds();
        Set<Department> departments = new HashSet<>(departmentRepository.findAllById(deptIds));
        if (departments.size() != deptIds.size()) {
            Set<UUID> found = departments.stream().map(Department::getId).collect(Collectors.toSet());
            deptIds.removeAll(found);
            throw new DepartmentNotFoundException("Departamentos não encontrados: " + deptIds);
        }

        Set<UUID> groupIds = request.getGroupIds();
        Set<UserGroup> userGroups = new HashSet<>(userGroupRepository.findAllById(groupIds));
        if (userGroups.size() != groupIds.size()) {
            Set<UUID> found = userGroups.stream().map(UserGroup::getId).collect(Collectors.toSet());
            groupIds.removeAll(found);
            throw new UserGroupNotFoundException("Grupos não encontrados: " + groupIds);
        }

        Set<UUID> functionIds = request.getFunctionIds();
        Set<Function> functions = new HashSet<>(functionRepository.findAllById(functionIds));
        if (functions.size() != functionIds.size()) {
            Set<UUID> found = functions.stream().map(Function::getId).collect(Collectors.toSet());
            functionIds.removeAll(found);
            throw new FunctionNotFoundException("Funções não encontradas: " + functionIds);
        }

        Position position = null;
        if (request.getPositionId() != null) {
            position = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new PositionNotFoundException("Posição não encontrada: " + request.getPositionId()));
        }

        UserStatus status = null;
        if (request.getStatusId() != null) {
            status = userStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new UserStatusNotFoundException("Status não encontrado: " + request.getStatusId()));
        }

        // Usuários relacionados
        User createdBy = request.getCreatedById() != null ?
                userRepository.findById(request.getCreatedById())
                        .orElseThrow(() -> new UserNotFoundException("Usuário criador não encontrado: " + request.getCreatedById()))
                : adminUser;

        User requestedBy = request.getRequestedById() != null ?
                userRepository.findById(request.getRequestedById())
                        .orElseThrow(() -> new UserNotFoundException("Usuário solicitante não encontrado: " + request.getRequestedById()))
                : null;

        // === Montagem do novo usuário ===

        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .fullName(request.getFullName())
                .socialName(request.getSocialName())
                .username(request.getUsername())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .birthDate(request.getBirthDate())
                .password(passwordEncoder.encode(request.getPassword()))
                .interfaceTheme(request.getInterfaceTheme())
                .timezone(request.getTimezone())
                .preferredLanguage(request.getPreferredLanguage())
                .invitationStatus(request.getInvitationStatus())
                .avatar(request.getAvatar())
                .origin(request.getOrigin())
                .privacyPolicyVersion(request.getPrivacyPolicyVersion())
                .cookieConsentStatus(request.getCookieConsentStatus())
                .managerId(request.getManagerId())
                .emailVerified(Boolean.TRUE.equals(request.getEmailVerified()))
                .twoFactorEnabled(Boolean.TRUE.equals(request.getTwoFactorEnabled()))
                .lastKnownLocation(request.getLastKnownLocation())
                .accountSuspendedReason(request.getAccountSuspendedReason())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(request.getCreatedById() != null ? userRepository.findById(request.getCreatedById()).orElse(null) : adminUser)
                .requestedBy(requestedBy)
                .roles(roles)
                .departments(departments)
                .userGroups(userGroups)
                .functions(functions)
                .position(position)
                .status(status)
                .personalPhoneNumbers(request.getPersonalPhoneNumbers() != null ? request.getPersonalPhoneNumbers() : Set.of())
                .address(convertToAddress(request.getAddress()))
                .build();

        userRepository.save(newUser);

        activityLogService.logAdminAction(
                adminUser,
                "Created new user: " + newUser.getUsername() + " (" + newUser.getEmail() + ")",
                newUser
        );
    }

    private Address convertToAddress(AddressDTO dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setCity(dto.getCity());
        address.setNeighborhood(dto.getNeighborhood());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        return address;
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
    private void validateUserCreation(RegisterUser request) {
        String email = request.getEmail();
        String username = request.getUsername();
        String password = request.getPassword();
        String cpf = request.getCpf();

        if (email == null || !isValidEmail(email)) {
            throw new InvalidFieldException("Formato de e-mail inválido: " + email);
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("O e-mail informado já está em uso: " + email);
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("O nome de usuário já está em uso: " + username);
        }

        if (cpf != null && userRepository.existsByCpf(cpf)) {
            throw new CpfAlreadyExistsException("O CPF informado já está em uso: " + cpf);
        }

        if (password == null || !isStrongPassword(password)) {
            throw new WeakPasswordException("A senha deve conter no mínimo 8 caracteres, incluindo letra maiúscula, minúscula e número.");
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
