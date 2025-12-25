package com.trans.asgard.domain.historiquevente.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriqueVenteResponse {
    private Long id;
    private LocalDate dateVente;
    private int quantiteVendue;

    private String jourSemaine;
    private int mois;
    private int annee;

    private Long productId;
    private Long entrepotId;
}
