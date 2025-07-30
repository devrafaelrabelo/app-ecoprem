package com.controlcenter.user.spec;

import com.controlcenter.entity.user.User;
import com.controlcenter.user.dto.AdminUserSearchParams;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;

public class UserSpecification {

    public static Specification<User> build(AdminUserSearchParams params) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (params.getNameOrEmail() != null && !params.getNameOrEmail().isBlank()) {
                String search = "%" + params.getNameOrEmail().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("fullName")), search),
                        cb.like(cb.lower(root.get("username")), search),
                        cb.like(cb.lower(root.get("email")), search)
                ));
            }

            if (params.getCpf() != null && !params.getCpf().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("cpf"), params.getCpf()));
            }

            if (params.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status").get("status"), params.getStatus()));
            }

            if (params.getRole() != null) {
                Join<Object, Object> roles = root.join("roles", JoinType.LEFT);
                predicate = cb.and(predicate, cb.equal(roles.get("name"), params.getRole()));
            }

            if (params.getDepartment() != null) {
                Join<Object, Object> departments = root.join("departments", JoinType.LEFT);
                predicate = cb.and(predicate, cb.equal(departments.get("name"), params.getDepartment()));
            }

            if (params.getPosition() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("position").get("name"), params.getPosition()));
            }

            if (params.getPreferredLanguage() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("preferredLanguage"), params.getPreferredLanguage()));
            }

            if (params.getInterfaceTheme() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("interfaceTheme"), params.getInterfaceTheme()));
            }

            if (params.getLocked() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("accountLocked"), params.getLocked()));
            }

            if (params.getEmailVerified() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("emailVerified"), params.getEmailVerified()));
            }

            if (params.getTwoFactorEnabled() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("twoFactorEnabled"), params.getTwoFactorEnabled()));
            }

            if (params.getFirstLogin() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("firstLogin"), params.getFirstLogin()));
            }

            if (params.getPasswordCompromised() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("passwordCompromised"), params.getPasswordCompromised()));
            }

            if (params.getCreatedFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), params.getCreatedFrom().atStartOfDay()));
            }

            if (params.getCreatedTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), params.getCreatedTo().atTime(LocalTime.MAX)));
            }

            if (params.getLastLoginFrom() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("lastLogin"), params.getLastLoginFrom().atStartOfDay()));
            }

            if (params.getLastLoginTo() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("lastLogin"), params.getLastLoginTo().atTime(LocalTime.MAX)));
            }

            return predicate;
        };
    }
}
