package com.trans.asgard.domain.stock.service;

import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import com.trans.asgard.domain.historiquevente.repository.HistoriqueVenteRepository;
import com.trans.asgard.domain.stock.model.Stock;
import com.trans.asgard.domain.stock.repository.StockRepository;
import com.trans.asgard.domain.stock.service.interfaces.StockService;
import com.trans.asgard.infrastructure.exception.custom.ResourceNotFoundException;
import com.trans.asgard.infrastructure.exception.custom.StockInsufficientException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final HistoriqueVenteRepository historiqueVenteRepository;

    @Transactional
    @Override
    public void sellProduct(Long stockId, int quantity) {

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(()-> new ResourceNotFoundException("Stock not found"));

        if(stock.getQuantity() < quantity ){
            throw new StockInsufficientException("Stock is not okay");
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);

        HistoriqueVente history = HistoriqueVente.builder()
                .dateVente(LocalDate.now())
                .quantiteVendue(quantity)
                .product(stock.getProduct())
                .entrepot(stock.getEntrepot())
                .build();

        historiqueVenteRepository.save(history);

    }
}
