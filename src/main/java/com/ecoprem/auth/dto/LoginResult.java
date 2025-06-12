package com.ecoprem.auth.dto;

import com.ecoprem.entity.user.User;

public record LoginResult(LoginWithRefreshResponse response, User user) {}