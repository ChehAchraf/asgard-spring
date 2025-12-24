package com.trans.asgard.domain.stock.controller;

import com.trans.asgard.domain.stock.dto.VenteRequest;
import com.trans.asgard.domain.stock.service.interfaces.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/vendre")
    @PreAuthorize("hasRole('GESTIONNAIRE')")
    public ResponseEntity<String> vendreProduit(@RequestBody VenteRequest request) {

        stockService.sellProduct(request.stockId(), request.quantity());

        return ResponseEntity.ok("sell operation has done, the historique is recorded.");
    }

}
