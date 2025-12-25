package com.trans.asgard.domain.prevision.service.interfaces;

import com.trans.asgard.domain.prevision.dto.PrevisionResponse;

public interface PrevisionService {
    PrevisionResponse generateAndSaveForecast(Long productId, Long warehouseId);
}
