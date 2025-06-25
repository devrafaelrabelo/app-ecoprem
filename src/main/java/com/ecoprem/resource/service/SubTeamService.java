package com.ecoprem.resource.service;

import com.ecoprem.entity.common.SubTeam;
import com.ecoprem.entity.common.Team;
import com.ecoprem.entity.user.User;
import com.ecoprem.resource.repository.SubTeamRepository;
import com.ecoprem.resource.repository.TeamRepository;
import com.ecoprem.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubTeamService {

    private final SubTeamRepository subTeamRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public SubTeam create(SubTeam subTeam, UUID teamId, UUID managerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));
        subTeam.setId(UUID.randomUUID());
        subTeam.setTeam(team);

        if (managerId != null) {
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new EntityNotFoundException("Manager not found"));
            subTeam.setManager(manager);
        }

        return subTeamRepository.save(subTeam);
    }

    public SubTeam update(UUID id, SubTeam subTeam, UUID teamId, UUID managerId) {
        SubTeam existing = subTeamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SubTeam not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        existing.setName(subTeam.getName());
        existing.setDescription(subTeam.getDescription());
        existing.setTeam(team);

        if (managerId != null) {
            User manager = userRepository.findById(managerId)
                    .orElseThrow(() -> new EntityNotFoundException("Manager not found"));
            existing.setManager(manager);
        } else {
            existing.setManager(null);
        }

        return subTeamRepository.save(existing);
    }

    public void delete(UUID id) {
        subTeamRepository.deleteById(id);
    }

    public SubTeam findById(UUID id) {
        return subTeamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SubTeam not found"));
    }

    public List<SubTeam> findAll() {
        return subTeamRepository.findAll();
    }
}
