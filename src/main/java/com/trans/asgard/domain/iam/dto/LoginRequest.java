package com.trans.asgard.domain.iam.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Login is required")
        String login,

        @NotBlank(message = "Password is required")
        String password
) {
}
