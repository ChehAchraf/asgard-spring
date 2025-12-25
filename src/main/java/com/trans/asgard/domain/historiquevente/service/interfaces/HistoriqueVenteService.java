package com.trans.asgard.domain.historiquevente.service.interfaces;

import com.trans.asgard.domain.historiquevente.dto.HistoriqueVenteDto;

import java.util.List;

public interface HistoriqueVenteService {
    HistoriqueVenteDto create(HistoriqueVenteDto dto);
    HistoriqueVenteDto getById(Long id);
    List<HistoriqueVenteDto> getAll();
    List<HistoriqueVenteDto> getByProductId(Long productId);
    List<HistoriqueVenteDto> getByEntrepotId(Long entrepotId);
    void delete(Long id);

}
