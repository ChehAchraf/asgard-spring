package com.trans.asgard.domain.Entrepot.repository;


import com.trans.asgard.domain.Entrepot.model.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrepotRepository extends JpaRepository<Entrepot , Long> {
    Optional<Entrepot> findByNom(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);
}
