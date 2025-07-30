package com.controlcenter.auth.dto;

import com.controlcenter.entity.user.User;

public record LoginResult(LoginWithRefreshResponse response, User user) {}