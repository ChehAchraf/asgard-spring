package com.trans.asgard.domain.prevision.service.intefaces;

import com.trans.asgard.domain.prevision.dto.PrevisionResponse;
import com.trans.asgard.domain.prevision.dto.PrevisionTestRequest;

public interface PrevisionService {
    PrevisionResponse generateAndSaveForecast(Long productId, Long warehouseId);
    PrevisionResponse generateAndSaveTestForecast(PrevisionTestRequest request);
}
