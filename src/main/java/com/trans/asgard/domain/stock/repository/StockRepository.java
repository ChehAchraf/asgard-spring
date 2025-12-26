package com.trans.asgard.domain.stock.repository;

import com.trans.asgard.domain.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock,Long> {
    Optional<Stock> findByProductIdAndEntrepotId(Long productId,Long entrepotId);
}
