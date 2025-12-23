package com.trans.asgard.domain.iam.dto;

import java.util.List;

public record JwtResponse(
        String token,
        String type,
        String login,
        List<String> roles
) {
    public JwtResponse(String token, String login, List<String> roles) {
        this(token, "Bearer", login, roles);
    }
}