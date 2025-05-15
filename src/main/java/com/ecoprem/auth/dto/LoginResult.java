package com.ecoprem.auth.dto;

import com.ecoprem.auth.entity.User;

public record LoginResult(LoginWithRefreshResponse response, User user) {}