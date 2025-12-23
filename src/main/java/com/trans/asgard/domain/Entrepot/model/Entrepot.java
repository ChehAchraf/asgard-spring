package com.trans.asgard.domain.Entrepot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "entrepots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, unique = true)
    private String nom;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String ville;

    @Size(max = 255)
    private String adresse;

    public void setNom(String nom) {
        this.nom = nom == null ? null : nom.trim().toLowerCase();
    }
}