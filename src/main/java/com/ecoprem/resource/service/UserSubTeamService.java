package com.ecoprem.resource.service;

import com.ecoprem.entity.common.SubTeam;
import com.ecoprem.entity.user.User;
import com.ecoprem.resource.repository.SubTeamRepository;
import com.ecoprem.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSubTeamService {

    private final SubTeamRepository subTeamRepository;
    private final UserRepository userRepository;

    public void addUserToSubTeam(UUID userId, UUID subTeamId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        SubTeam subTeam = subTeamRepository.findById(subTeamId)
                .orElseThrow(() -> new EntityNotFoundException("SubTeam not found"));

        subTeam.getUsers().add(user);
        subTeamRepository.save(subTeam);
    }

    public void removeUserFromSubTeam(UUID userId, UUID subTeamId) {
        SubTeam subTeam = subTeamRepository.findById(subTeamId)
                .orElseThrow(() -> new EntityNotFoundException("SubTeam not found"));

        subTeam.getUsers().removeIf(u -> u.getId().equals(userId));
        subTeamRepository.save(subTeam);
    }
}
