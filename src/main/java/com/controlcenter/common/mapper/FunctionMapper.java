package com.controlcenter.common.mapper;

import com.controlcenter.common.dto.FunctionRequestDTO;
import com.controlcenter.common.dto.FunctionResponseDTO;
import com.controlcenter.entity.common.Department;
import com.controlcenter.entity.common.Function;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FunctionMapper {
    public Function toEntity(FunctionRequestDTO dto, Department department) {
        Function function = new Function();
        function.setId(UUID.randomUUID());
        function.setName(dto.name());
        function.setDescription(dto.description());
        function.setDepartment(department);
        return function;
    }

    public FunctionResponseDTO toDTO(Function function) {
        return FunctionResponseDTO.builder()
                .id(function.getId())
                .name(function.getName())
                .description(function.getDescription())
                .departmentId(function.getDepartment() != null ? function.getDepartment().getId() : null)
                .departmentName(function.getDepartment() != null ? function.getDepartment().getName() : null)
                .build();
    }
}