package com.controlcenter.common.service;

import com.controlcenter.common.repository.DepartmentRepository;
import com.controlcenter.common.dto.FunctionRequestDTO;
import com.controlcenter.common.dto.FunctionResponseDTO;
import com.controlcenter.common.repository.FunctionRepository;
import com.controlcenter.entity.common.Department;
import com.controlcenter.entity.common.Function;
import com.controlcenter.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminFunctionService {

    private final FunctionRepository functionRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<FunctionResponseDTO> findAll() {
        return functionRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public FunctionResponseDTO findById(UUID id) {
        Function function = functionRepository.findWithDepartmentById(id)
                .orElseThrow(() -> new RuntimeException("Função não encontrada"));
        return toDTO(function);
    }

    @Transactional
    public FunctionResponseDTO create(FunctionRequestDTO dto) {
        Department department = departmentRepository.getReferenceById(dto.departmentId());

        Function function = new Function();
        function.setName(dto.name());
        function.setDescription(dto.description());
        function.setDepartment(department);

        Function saved = functionRepository.save(function); // persistência normal com flush no final da transação

        return toDTO(saved);
    }

    @Transactional
    public FunctionResponseDTO update(UUID id, FunctionRequestDTO dto) {
        Function function = functionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Função não encontrada"));

        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Departamento não encontrado"));

        function.setName(dto.name());
        function.setDescription(dto.description());

        // Evita problemas de merge no update
        if (!Objects.equals(function.getDepartment(), department)) {
            function.setDepartment(department);
        }

        // Nenhum save() necessário, pois a entidade já está gerenciada no contexto transacional
        return toDTO(function);
    }

    public void delete(UUID id) {
        if (!functionRepository.existsById(id)) {
            throw new RuntimeException("Função não encontrada");
        }
        functionRepository.deleteById(id);
    }

    private FunctionResponseDTO toDTO(Function function) {
        return FunctionResponseDTO.builder()
                .id(function.getId())
                .name(function.getName())
                .description(function.getDescription())
                .departmentId(function.getDepartment() != null ? function.getDepartment().getId() : null)
                .departmentName(function.getDepartment() != null ? function.getDepartment().getName() : null)
                .build();
    }

}