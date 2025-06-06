package com.ecoprem.auth.dto;

import com.ecoprem.entity.User;

public record LoginResult(LoginWithRefreshResponse response, User user) {}