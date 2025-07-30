package com.controlcenter.resource.controller;

import com.controlcenter.resource.dto.UserSubTeamRequest;
import com.controlcenter.resource.service.UserSubTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resource/subteams/users")
@RequiredArgsConstructor
public class UserSubTeamController {

    private final UserSubTeamService userSubTeamService;

    @PostMapping("/add")
    public ResponseEntity<Void> addUser(@RequestBody UserSubTeamRequest request) {
        userSubTeamService.addUserToSubTeam(request.userId(), request.subTeamId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeUser(@RequestBody UserSubTeamRequest request) {
        userSubTeamService.removeUserFromSubTeam(request.userId(), request.subTeamId());
        return ResponseEntity.ok().build();
    }
}
