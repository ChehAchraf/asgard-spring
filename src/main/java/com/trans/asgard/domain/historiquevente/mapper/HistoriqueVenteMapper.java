package com.trans.asgard.domain.historiquevente.mapper;

import com.trans.asgard.domain.historiquevente.dto.HistoriqueVenteDto;
import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HistoriqueVenteMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "entrepotId", source = "entrepot.id")
    HistoriqueVenteDto toDto(HistoriqueVente entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "entrepot", ignore = true)
    @Mapping(target = "jourSemaine", expression = "java(dto.getDateVente() != null ? dto.getDateVente().getDayOfWeek().toString() : null)")
    @Mapping(target = "mois", expression = "java(dto.getDateVente() != null ? dto.getDateVente().getMonthValue() : 0)")
    @Mapping(target = "annee", expression = "java(dto.getDateVente() != null ? dto.getDateVente().getYear() : 0)")
    HistoriqueVente toEntity(HistoriqueVenteDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(HistoriqueVenteDto dto, @MappingTarget HistoriqueVente entity);
}
