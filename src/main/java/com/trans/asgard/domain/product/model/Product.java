package com.trans.asgard.domain.product.model;


import com.trans.asgard.domain.stock.model.Stock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;
    private String categorie;
    private double prixVente;
    private double prixAchat ;
    private double marge ;
    private double poids;
    private String unite;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Stock> stocks;



}

