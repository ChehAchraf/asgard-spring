package com.trans.asgard.domain.stock.service;

import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.Entrepot.repository.EntrepotRepository;
import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import com.trans.asgard.domain.historiquevente.repository.HistoriqueVenteRepository;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.product.repository.ProductRepository;
import com.trans.asgard.domain.stock.model.Stock;
import com.trans.asgard.domain.stock.repository.StockRepository;
import com.trans.asgard.domain.stock.service.interfaces.StockService;
import com.trans.asgard.infrastructure.exception.custom.ResourceNotFoundException;
import com.trans.asgard.infrastructure.exception.custom.StockInsufficientException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final HistoriqueVenteRepository historiqueVenteRepository;
    private final ProductRepository productRepository;
    private final EntrepotRepository entrepotRepository;

    @Transactional
    @Override
    public void sellProduct(Long stockId, int quantity) {

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        if (stock.getQuantity() < quantity) {
            throw new StockInsufficientException("Stock is not okay");
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);

        LocalDate today = LocalDate.now();

        HistoriqueVente history = HistoriqueVente.builder()
                .dateVente(LocalDate.now())
                .quantiteVendue(quantity)
                .product(stock.getProduct())
                .entrepot(stock.getEntrepot())
                .jourSemaine(today.getDayOfWeek().toString())
                .mois(today.getMonthValue())
                .annee(today.getYear())
                .build();

        historiqueVenteRepository.save(history);

    }

    @Override
    public void addProduct(Long productId, Long entrepotId, int quantityAdded) {

        Optional<Stock> stockOptional = stockRepository.findByProductIdAndEntrepotId(productId, entrepotId);

        if (stockOptional.isPresent()) {
            Stock stock = stockOptional.get();
            stock.setQuantity(stock.getQuantity() + quantityAdded);
            stockRepository.save(stock);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            Entrepot entrepot = entrepotRepository.findById(entrepotId)
                    .orElseThrow(() -> new ResourceNotFoundException("Entrepot not found"));

            Stock newStock = Stock.builder()
                    .product(product)
                    .entrepot(entrepot)
                    .quantity(quantityAdded)
                    .alertThreshold(10)
                    .build();

            stockRepository.save(newStock);
        }
    }
}
