package com.trans.asgard.domain.Entrepot.mapper;

import com.trans.asgard.domain.Entrepot.dto.EntrepotRequestDto;
import com.trans.asgard.domain.Entrepot.dto.EntrepotResponseDto;
import com.trans.asgard.domain.Entrepot.model.Entrepot;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntrepotMapper {

    Entrepot toEntity(EntrepotRequestDto dto);

    @Mapping(target = "id", source = "id")
    EntrepotResponseDto toResponseDto(Entrepot entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(EntrepotRequestDto dto, @MappingTarget Entrepot entity);
}
