package com.trans.asgard.domain.iam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @NotBlank
    private Long id;
    @NotBlank(message = "Nom is required")
    private String nom;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Categorie is required")
    private String categorie;

    @NotNull(message = "Prix de vente is required")
    @Positive(message = "Prix de vente must be positive")
    private Double prixVente;

    @NotNull(message = "Prix d'achat is required")
    @Positive(message = "Prix d'achat must be positive")
    private Double prixAchat;

    @NotNull(message = "Marge is required")
    @PositiveOrZero(message = "Marge must be zero or positive")
    private Double marge;

    @NotNull(message = "Poids is required")
    @Positive(message = "Poids must be positive")
    private Double poids;

    @NotBlank(message = "Unite is required")
    private String unite;


}

