package com.trans.asgard.domain.historiquevente.model;

import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "historiqueVente")
@Data
@Builder
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
    private Product product;

    @ManyToOne
    private Entrepot entrepot;
}
