package com.trans.asgard.domain.iam.model;

import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.iam.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String login;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "entrepot_id")
    private Entrepot entrepotAssigne;

}
