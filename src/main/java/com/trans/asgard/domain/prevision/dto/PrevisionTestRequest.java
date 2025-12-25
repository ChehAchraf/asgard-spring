package com.trans.asgard.domain.prevision.dto;

public record PrevisionTestRequest(
        Long produitId,
        Long entrepotId,
        String produitNom,
        Integer stockActuel,
        String historiqueVentesJson
) {
}
