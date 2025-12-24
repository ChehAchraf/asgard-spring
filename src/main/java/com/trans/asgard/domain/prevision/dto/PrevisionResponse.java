package com.trans.asgard.domain.prevision.dto;

import java.time.LocalDate;

public record PrevisionResponse(
        Long id,
        Long produitId,
        Long entrepotId,
        LocalDate datePrevision,
        Integer quantiteEstimee30Jours,
        String recommandation,
        Integer niveauConfiance,
        String detailsJson
) {
}
