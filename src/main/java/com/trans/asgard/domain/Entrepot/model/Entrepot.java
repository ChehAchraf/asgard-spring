package com.trans.asgard.domain.Entrepot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trans.asgard.domain.iam.model.User;
import com.trans.asgard.domain.stock.model.Stock;
import jakarta.persistence.*;
import java.util.List;
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
    @Column(nullable = false)
    private String nom;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String ville;

    @Size(max = 255)
    private String adresse;

    @OneToMany(mappedBy = "entrepot", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Stock> stocks;

    @OneToMany(mappedBy = "entrepotAssigne")
    @JsonIgnore
    private List<User> gestionnaires;
}