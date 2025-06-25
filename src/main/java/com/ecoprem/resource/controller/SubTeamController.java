package com.ecoprem.resource.controller;

import com.ecoprem.entity.common.SubTeam;
import com.ecoprem.resource.dto.SubTeamDTO;
import com.ecoprem.resource.mapper.SubTeamMapper;
import com.ecoprem.resource.service.SubTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resource/workforce/subteams")
@RequiredArgsConstructor
public class SubTeamController {

    private final SubTeamService subTeamService;

    @PostMapping
    public ResponseEntity<SubTeamDTO> create(@RequestBody SubTeamDTO dto) {
        SubTeam subTeam = SubTeamMapper.toEntity(dto);
        SubTeam saved = subTeamService.create(subTeam, dto.teamId(), dto.managerId());
        return ResponseEntity.ok(SubTeamMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubTeamDTO> update(@PathVariable UUID id, @RequestBody SubTeamDTO dto) {
        SubTeam subTeam = SubTeamMapper.toEntity(dto);
        SubTeam updated = subTeamService.update(id, subTeam, dto.teamId(), dto.managerId());
        return ResponseEntity.ok(SubTeamMapper.toDTO(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubTeam> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(subTeamService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<SubTeam>> findAll() {
        return ResponseEntity.ok(subTeamService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        subTeamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
