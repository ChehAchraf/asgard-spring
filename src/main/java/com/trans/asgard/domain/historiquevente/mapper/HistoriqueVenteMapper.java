package com.trans.asgard.domain.historiquevente.mapper;

import com.trans.asgard.domain.historiquevente.dto.HistoriqueVenteDto;
import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HistoriqueVenteMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "entrepotId", source = "entrepot.id")
    HistoriqueVenteDto toResponse(HistoriqueVente entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "entrepot", ignore = true)
    @Mapping(target = "jourSemaine", ignore = true)
    @Mapping(target = "mois", ignore = true)
    @Mapping(target = "annee", ignore = true)
    HistoriqueVente toEntity(HistoriqueVenteDto dto);

}
