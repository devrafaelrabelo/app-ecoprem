package com.ecoprem.auth.service;

import com.ecoprem.auth.dto.RegisterRequest;
import com.ecoprem.auth.entity.Role;
import com.ecoprem.auth.entity.User;
import com.ecoprem.auth.exception.EmailAlreadyExistsException;
import com.ecoprem.auth.exception.RoleNotFoundException;
import com.ecoprem.auth.exception.UsernameAlreadyExistsException;
import com.ecoprem.auth.repository.RoleRepository;
import com.ecoprem.auth.repository.UserRepository;
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

    public void createUserByAdmin(RegisterRequest request) {

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

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + request.getRole()));

        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setFullName(
                request.getFullName() != null ? request.getFullName() :
                        request.getFirstName() + " " + request.getLastName()
        );
        newUser.setSocialName(request.getSocialName());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(role);
        newUser.setEmailVerified(true); // Admin-created users podem ser verificados direto, se quiser
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(newUser);
    }
}
