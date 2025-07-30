package com.controlcenter.resource.service;

import com.controlcenter.entity.common.SubTeam;
import com.controlcenter.entity.common.Team;
import com.controlcenter.resource.repository.SubTeamRepository;
import com.controlcenter.resource.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubTeamTeamService {

    private final SubTeamRepository subTeamRepository;
    private final TeamRepository teamRepository;

    public void assignSubTeamToTeam(UUID subTeamId, UUID teamId) {
        SubTeam subTeam = subTeamRepository.findById(subTeamId)
                .orElseThrow(() -> new EntityNotFoundException("SubTeam not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        subTeam.setTeam(team);
        subTeamRepository.save(subTeam);
    }

    public void removeSubTeamFromTeam(UUID subTeamId) {
        SubTeam subTeam = subTeamRepository.findById(subTeamId)
                .orElseThrow(() -> new EntityNotFoundException("SubTeam not found"));

        subTeam.setTeam(null); // Remover associação
        subTeamRepository.save(subTeam);
    }
}
