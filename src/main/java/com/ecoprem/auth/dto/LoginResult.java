package com.ecoprem.auth.dto;

import com.ecoprem.entity.auth.User;

public record LoginResult(LoginWithRefreshResponse response, User user) {}