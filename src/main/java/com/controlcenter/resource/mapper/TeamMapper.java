package com.controlcenter.resource.mapper;

import com.controlcenter.entity.common.Team;
import com.controlcenter.resource.dto.TeamDTO;

public class TeamMapper {

    public static TeamDTO toDTO(Team team) {
        return new TeamDTO(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getLocation(),
                team.getSupervisor() != null ? team.getSupervisor().getId() : null
        );
    }

    public static Team toEntity(TeamDTO dto) {
        Team team = new Team();
        team.setId(dto.id());
        team.setName(dto.name());
        team.setDescription(dto.description());
        team.setLocation(dto.location());
        // supervisor deve ser setado pelo serviço, buscando por ID
        return team;
    }
}
