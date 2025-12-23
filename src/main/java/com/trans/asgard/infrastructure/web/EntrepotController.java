package com.trans.asgard.infrastructure.web;

import com.trans.asgard.application.dto.Entrepot.EntrepotRequestDto;
import com.trans.asgard.application.dto.Entrepot.EntrepotResponseDto;
import com.trans.asgard.application.service.interfaces.EntrepotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrepots")
@RequiredArgsConstructor
public class EntrepotController {

    private final EntrepotService entrepotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntrepotResponseDto> create(@Valid @RequestBody EntrepotRequestDto dto) {
        return new ResponseEntity<>(entrepotService.createEntrepot(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrepotResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entrepotService.getEntrepotById(id));
    }

    @GetMapping
    public ResponseEntity<List<EntrepotResponseDto>> getAll() {
        return ResponseEntity.ok(entrepotService.getAllEntrepots());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntrepotResponseDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody EntrepotRequestDto dto) {
        return ResponseEntity.ok(entrepotService.updateEntrepot(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        entrepotService.deleteEntrepot(id);
        return ResponseEntity.noContent().build();
    }
}
