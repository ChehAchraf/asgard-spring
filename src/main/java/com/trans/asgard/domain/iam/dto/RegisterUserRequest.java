package com.trans.asgard.domain.iam.dto;

import com.trans.asgard.domain.iam.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "the login is required")
        String login,
        @NotBlank(message = "the nom is required")
        String nom,
        @NotBlank(message = "the prenom is required")
        String prenom,
        @NotBlank(message = "The email is required")
        String email,
        @Size(min = 6, message = "the password must be greater than 6 character")
        String password,
        @NotNull(message = "the role is required")
        Role role
) {
}
