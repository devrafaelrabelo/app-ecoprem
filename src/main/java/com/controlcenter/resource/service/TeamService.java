package com.controlcenter.resource.service;

import com.controlcenter.entity.common.Team;
import com.controlcenter.entity.user.User;
import com.controlcenter.resource.repository.TeamRepository;
import com.controlcenter.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public Team create(Team team, UUID supervisorId) {
        team.setId(UUID.randomUUID());

        if (supervisorId != null) {
            User supervisor = userRepository.findById(supervisorId)
                    .orElseThrow(() -> new EntityNotFoundException("Supervisor not found"));
            team.setSupervisor(supervisor);
        }

        return teamRepository.save(team);
    }

    public Team update(UUID id, Team updatedTeam, UUID supervisorId) {
        Team existing = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        existing.setName(updatedTeam.getName());
        existing.setDescription(updatedTeam.getDescription());
        existing.setLocation(updatedTeam.getLocation());

        if (supervisorId != null) {
            User supervisor = userRepository.findById(supervisorId)
                    .orElseThrow(() -> new EntityNotFoundException("Supervisor not found"));
            existing.setSupervisor(supervisor);
        } else {
            existing.setSupervisor(null);
        }

        return teamRepository.save(existing);
    }

    public void delete(UUID id) {
        teamRepository.deleteById(id);
    }

    public Team findById(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }
}
