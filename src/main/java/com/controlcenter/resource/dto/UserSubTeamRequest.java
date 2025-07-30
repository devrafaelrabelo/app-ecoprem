package com.controlcenter.resource.dto;

import java.util.UUID;

public record UserSubTeamRequest(UUID userId, UUID subTeamId) {}
