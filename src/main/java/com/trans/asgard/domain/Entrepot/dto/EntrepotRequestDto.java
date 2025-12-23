package com.trans.asgard.domain.Entrepot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EntrepotRequestDto(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 2, max = 100)
        String nom,

        @NotBlank(message = "La ville est obligatoire")
        @Size(max = 100)
        String ville,

        @Size(max = 255)
        String adresse
) {
}
