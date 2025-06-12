//package com.ecoprem.auth.service;
//
//import com.ecoprem.user.repository.UserRepository;
//import com.ecoprem.entity.user.User;
//import com.ecoprem.entity.security.Permission;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class PermissionResolverService {
//
//    private final UserRepository userRepository;
//
//    public Set<String> resolvePermissionNames(User user) {
//        Set<Permission> allPermissions = new HashSet<>();
//
//        // 1. Permissões via Roles
//        user.getRoles().forEach(role -> {
//            if (role.getPermissions() != null) {
//                allPermissions.addAll(role.getPermissions());
//            }
//        });
//
//        // 2. Permissões diretas
//        if (user.getDirectPermissions() != null) {
//            allPermissions.addAll(user.getDirectPermissions());
//        }
//
//        // 3. (Futuro) Permissões via grupos, funções etc.
//
//        return allPermissions.stream()
//                .map(Permission::getName)
//                .collect(Collectors.toSet());
//    }
//
//    public boolean hasPermission(User user, String permissionName) {
//        return resolvePermissionNames(user).contains(permissionName);
//    }
//}
