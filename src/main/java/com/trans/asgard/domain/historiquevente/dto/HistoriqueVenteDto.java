package com.trans.asgard.domain.historiquevente.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueVenteDto {
    private Long id;
    @NotNull(message = "La date de vente est obligatoire")
    @PastOrPresent(message = "La date de vente ne peut pas être dans le futur")
    private LocalDate dateVente;

    @Min(value = 1, message = "La quantité vendue doit être au moins 1")
    private int quantiteVendue;

    private String jourSemaine;

    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    private int mois;

    @Min(value = 2000, message = "L'année doit être réaliste")
    private int annee;

    @NotNull(message = "Le produit est obligatoire")
    private Long productId;

    @NotNull(message = "L'entrepôt est obligatoire")
    private Long entrepotId;



}
