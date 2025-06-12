//package com.ecoprem.auth.service;
//
//import com.ecoprem.entity.user.User;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class PermissionService {
//
//    public boolean hasPermission(User user, String permissionName) {
//        return user.getRoles().stream()
//                .flatMap(role -> role.getPermissions().stream())
//                .anyMatch(p -> p.getName().equals(permissionName)) ||
//                user.getDirectPermissions().stream()
//                        .anyMatch(p -> p.getName().equals(permissionName));
//    }
//
//    public Set<String> getEffectivePermissionNames(User user) {
//        Set<String> permissions = new HashSet<>();
//        user.getRoles().forEach(role -> role.getPermissions()
//                .forEach(p -> permissions.add(p.getName())));
//        user.getDirectPermissions().forEach(p -> permissions.add(p.getName()));
//        return permissions;
//    }
//}
//
