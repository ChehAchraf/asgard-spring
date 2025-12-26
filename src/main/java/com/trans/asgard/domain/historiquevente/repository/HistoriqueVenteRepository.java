package com.trans.asgard.domain.historiquevente.repository;

import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HistoriqueVenteRepository extends JpaRepository<HistoriqueVente,Long> {

    List<HistoriqueVente> findByProductId(Long productId);
    List<HistoriqueVente> findByEntrepotId(Long entrepotId);
    List<HistoriqueVente> findByProductIdAndEntrepotIdAndDateVenteBetween(
            Long productId,
            Long entrepotId,
            LocalDate startDate,
            LocalDate endDate
    );
}
