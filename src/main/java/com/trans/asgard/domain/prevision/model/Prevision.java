package com.trans.asgard.domain.prevision.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "previsions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long produitId;

    @Column(nullable = false)
    private Long entrepotId;

    @Column(nullable = false)
    private LocalDate datePrevision;

    @Column(nullable = false)
    private Integer quantiteEstimee30Jours;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String recommandation;

    @Column(nullable = false)
    private Integer niveauConfiance;

    @Column(columnDefinition = "TEXT")
    private String detailsJson;
}