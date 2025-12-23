package com.trans.asgard.domain.product.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
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


//    @OneToMany(mappedBy="product" , cascade = CascadeType.ALL,orphanRemoval = true)
//    private List<Stock> stocks;
}

