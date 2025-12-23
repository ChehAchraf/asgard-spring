package com.trans.asgard.domain.Entrepot.service.interfaces;

import com.trans.asgard.domain.Entrepot.dto.EntrepotRequestDto;
import com.trans.asgard.domain.Entrepot.dto.EntrepotResponseDto;
import com.trans.asgard.domain.Entrepot.model.Entrepot;

import java.util.List;

public interface EntrepotService {

    EntrepotResponseDto createEntrepot(EntrepotRequestDto dto);
    EntrepotResponseDto updateEntrepot(Long id, EntrepotRequestDto dto);
    void deleteEntrepot(Long id);
    EntrepotResponseDto getEntrepotById(Long id);
    List<EntrepotResponseDto> getAllEntrepots();

    Entrepot getEntrepotEntityByIdOrThrow(Long id);
}
