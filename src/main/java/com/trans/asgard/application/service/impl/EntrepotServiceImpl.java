package com.trans.asgard.application.service.impl;

import com.trans.asgard.application.dto.Entrepot.EntrepotRequestDto;
import com.trans.asgard.application.dto.Entrepot.EntrepotResponseDto;
import com.trans.asgard.application.mapper.EntrepotMapper;
import com.trans.asgard.application.service.interfaces.EntrepotService;
import com.trans.asgard.domain.model.Entrepot;
import com.trans.asgard.domain.repository.EntrepotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EntrepotServiceImpl implements EntrepotService {

    private final EntrepotRepository entrepotRepository;
    private final EntrepotMapper entrepotMapper;

    @Override
    public EntrepotResponseDto createEntrepot(EntrepotRequestDto dto) {
        Entrepot entrepot = entrepotMapper.toEntity(dto);
        entrepot = entrepotRepository.save(entrepot);

        return entrepotMapper.toResponseDto(entrepot);
    }

    @Override
    public EntrepotResponseDto updateEntrepot(Long id, EntrepotRequestDto dto) {
        Entrepot entrepot = getEntrepotEntityByIdOrThrow(id);

        String newNom = dto.nom();
        if (entrepotRepository.existsByNomIgnoreCaseAndIdNot(newNom, id)) {
            throw new IllegalStateException("Un autre entrepôt utilise déjà ce nom : " + newNom);
        }


        entrepotMapper.updateFromDto(dto, entrepot);

        entrepot = entrepotRepository.save(entrepot);
        return entrepotMapper.toResponseDto(entrepot);
    }

    @Override
    public void deleteEntrepot(Long id) {
        if (!entrepotRepository.existsById(id)) {
            throw new RuntimeException("Entrepôt non trouvé avec l'ID : " + id);
        }
        entrepotRepository.deleteById(id);
    }

    @Override
    public EntrepotResponseDto getEntrepotById(Long id) {
        Entrepot entrepot = getEntrepotEntityByIdOrThrow(id);
        return entrepotMapper.toResponseDto(entrepot);
    }

    @Override
    public List<EntrepotResponseDto> getAllEntrepots() {
        return entrepotRepository.findAll()
                .stream()
                .map(entrepotMapper::toResponseDto)
                .toList();
    }

    @Override
    public Entrepot getEntrepotEntityByIdOrThrow(Long id) {
        return entrepotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrepôt non trouvé avec l'ID : " + id));
    }
}
