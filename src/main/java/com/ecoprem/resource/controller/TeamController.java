package com.ecoprem.resource.controller;

import com.ecoprem.entity.common.Team;
import com.ecoprem.resource.dto.TeamDTO;
import com.ecoprem.resource.mapper.TeamMapper;
import com.ecoprem.resource.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resource/workforce/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamDTO> create(@RequestBody TeamDTO dto) {
        Team team = TeamMapper.toEntity(dto);
        Team saved = teamService.create(team, dto.supervisorId());
        return ResponseEntity.ok(TeamMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> update(@PathVariable UUID id, @RequestBody TeamDTO dto) {
        Team team = TeamMapper.toEntity(dto);
        Team updated = teamService.update(id, team, dto.supervisorId());
        return ResponseEntity.ok(TeamMapper.toDTO(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(teamService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Team>> findAll() {
        return ResponseEntity.ok(teamService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
