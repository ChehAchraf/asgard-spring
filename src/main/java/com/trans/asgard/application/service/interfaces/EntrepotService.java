package com.trans.asgard.application.service.interfaces;

import com.trans.asgard.application.dto.Entrepot.EntrepotRequestDto;
import com.trans.asgard.application.dto.Entrepot.EntrepotResponseDto;
import com.trans.asgard.domain.model.Entrepot;

import java.util.List;

public interface EntrepotService {

    EntrepotResponseDto createEntrepot(EntrepotRequestDto dto);
    EntrepotResponseDto updateEntrepot(Long id, EntrepotRequestDto dto);
    void deleteEntrepot(Long id);
    EntrepotResponseDto getEntrepotById(Long id);
    List<EntrepotResponseDto> getAllEntrepots();

    Entrepot getEntrepotEntityByIdOrThrow(Long id);
}
