package com.trans.asgard.application.mapper;

import com.trans.asgard.application.dto.Entrepot.EntrepotRequestDto;
import com.trans.asgard.application.dto.Entrepot.EntrepotResponseDto;
import com.trans.asgard.domain.model.Entrepot;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntrepotMapper {

    Entrepot toEntity(EntrepotRequestDto dto);

    @Mapping(target = "id", source = "id")
    EntrepotResponseDto toResponseDto(Entrepot entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(EntrepotRequestDto dto, @MappingTarget Entrepot entity);
}
