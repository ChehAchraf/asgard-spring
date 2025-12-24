package com.trans.asgard.domain.historiquevente.model;

import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "historiqueVente")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class HistoriqueVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dateVente;
    private int quantiteVendue;
    private String jourSemaine;
    private int mois;
    private int annee;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)

    private Product product;

    @ManyToOne
    @JoinColumn(name = "entrepot_id", nullable = false)

    private Entrepot entrepot;


    @PrePersist
    @PreUpdate
    private void computeDateFields() {
        if (dateVente != null) {
            this.jourSemaine = dateVente.getDayOfWeek().toString();
            this.mois = dateVente.getMonthValue();
            this.annee = dateVente.getYear();
        }
    }
}
