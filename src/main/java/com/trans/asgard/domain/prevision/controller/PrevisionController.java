package com.trans.asgard.domain.prevision.controller;

import com.trans.asgard.domain.prevision.dto.PrevisionResponse;
import com.trans.asgard.domain.prevision.dto.PrevisionTestRequest;
import com.trans.asgard.domain.prevision.service.intefaces.PrevisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prevision")
@RequiredArgsConstructor
public class PrevisionController {

    private final PrevisionService previsionService;

    @PostMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<PrevisionResponse> generateForecast(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(previsionService.generateAndSaveForecast(productId, warehouseId));
    }
}