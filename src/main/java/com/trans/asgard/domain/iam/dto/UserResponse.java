package com.trans.asgard.domain.iam.dto;

import com.trans.asgard.domain.iam.enums.Role;


public record UserResponse(
        String id,
        String login,
        String nom,
        String prenom,
        String email,
        Role role
) {
}