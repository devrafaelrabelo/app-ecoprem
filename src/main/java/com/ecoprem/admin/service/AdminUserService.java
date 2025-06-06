package com.ecoprem.admin.service;

import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.auth.entity.Role;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.exception.EmailAlreadyExistsException;
import com.ecoprem.auth.exception.RoleNotFoundException;
import com.ecoprem.auth.exception.UsernameAlreadyExistsException;
import com.ecoprem.auth.repository.RoleRepository;
import com.ecoprem.auth.repository.UserRepository;
import com.ecoprem.auth.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void createUserByAdmin(RegisterRequest request, User adminUser) {
        validateUserCreation(request);

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + request.getRole()));

        User newUser = buildUserFromRequest(request, role);

        userRepository.save(newUser);

        activityLogService.logAdminAction(
                adminUser,
                "Created new user: " + newUser.getUsername() + " (" + newUser.getEmail() + ")",
                newUser
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

    private User buildUserFromRequest(RegisterRequest request, Role role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setFullName(request.getFullName() != null
                ? request.getFullName()
                : request.getFirstName() + " " + request.getLastName());
        user.setSocialName(request.getSocialName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEmailVerified(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}
