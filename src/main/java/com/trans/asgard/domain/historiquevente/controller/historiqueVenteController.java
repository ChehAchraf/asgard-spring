package com.trans.asgard.domain.historiquevente.controller;

import com.trans.asgard.domain.historiquevente.dto.HistoriqueVenteDto;
import com.trans.asgard.domain.historiquevente.service.interfaces.HistoriqueVenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/historiqueVente")
@RequiredArgsConstructor
public class historiqueVenteController {

    private  final HistoriqueVenteService historiqueVenteService;

    @GetMapping
    public ResponseEntity<List<HistoriqueVenteDto>> getAllHistoriqueVente()
    {
        List<HistoriqueVenteDto>  historiqueVente = historiqueVenteService.getAll();
        return  ResponseEntity.ok(historiqueVente);
    }


    @GetMapping("/{id}")
    public ResponseEntity<HistoriqueVenteDto> getHistoriqueVenteId(@PathVariable Long id)
    {
        HistoriqueVenteDto historiqueVente = historiqueVenteService.getById(id);
        return  ResponseEntity.ok(historiqueVente);
    }


    @GetMapping("/Product/{id}")
    public ResponseEntity<List<HistoriqueVenteDto>>getHistoriqueVenteProductId(@PathVariable Long id)
    {
        List<HistoriqueVenteDto>  historiqueVente = historiqueVenteService.getByProductId(id);
        return  ResponseEntity.ok(historiqueVente);
    }


    @GetMapping("/Entrepot/{id}")
    public ResponseEntity<List<HistoriqueVenteDto>> getHistoriqueVenteEntrepotId(@PathVariable Long id)
    {
        List<HistoriqueVenteDto>  historiqueVente = historiqueVenteService.getByEntrepotId(id);
        return  ResponseEntity.ok(historiqueVente);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistoriqueVente(@PathVariable Long id) {
        historiqueVenteService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
