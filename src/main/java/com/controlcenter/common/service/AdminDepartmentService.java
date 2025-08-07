package com.controlcenter.common.service;

import com.controlcenter.common.repository.DepartmentRepository;
import com.controlcenter.common.dto.DepartmentCreateDTO;
import com.controlcenter.common.dto.DepartmentDTO;
import com.controlcenter.common.dto.DepartmentUpdateDTO;
import com.controlcenter.entity.common.Department;
import com.controlcenter.entity.user.User;
import com.controlcenter.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public List<DepartmentDTO> findAll() {
        return departmentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DepartmentDTO findById(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return toDTO(department);
    }

    public DepartmentDTO create(DepartmentCreateDTO dto) {
        Department department = new Department();
        department.setId(UUID.randomUUID());
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());

        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            department.setManager(manager);
        }

        return toDTO(departmentRepository.save(department));
    }

    public DepartmentDTO update(UUID id, DepartmentUpdateDTO dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        department.setName(dto.getName());
        department.setDescription(dto.getDescription());

        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            department.setManager(manager);
        } else {
            department.setManager(null);
        }

        return toDTO(departmentRepository.save(department));
    }

    public void delete(UUID id) {
        departmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UUID> getUserIds(UUID departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return department.getUsers().stream()
                .map(User::getId)
                .toList();
    }

    @Transactional
    public void updateUsers(UUID departmentId, List<UUID> userIds) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        List<User> users = userRepository.findAllById(userIds);
        department.setUsers(users);

        departmentRepository.save(department);
    }

    private DepartmentDTO toDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        if (department.getManager() != null) {
            dto.setManagerId(department.getManager().getId());
        }
        return dto;
    }

    public void assignManager(UUID departmentId, UUID managerId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (managerId != null) {
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            department.setManager(manager);
        } else {
            department.setManager(null);
        }

        departmentRepository.save(department);
    }
}