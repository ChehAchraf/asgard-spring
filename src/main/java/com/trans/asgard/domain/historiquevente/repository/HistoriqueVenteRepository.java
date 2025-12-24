package com.trans.asgard.domain.historiquevente.repository;

import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueVenteRepository extends JpaRepository<HistoriqueVente,Long> {

    List<HistoriqueVente> findByProductId(Long productId);
    List<HistoriqueVente> findByEntrepotId(Long entrepotId);
}
