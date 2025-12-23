package com.trans.asgard.application.dto.Entrepot;

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
