package com.trans.asgard.domain.stock.repository;

import com.trans.asgard.domain.stock.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock,Long> {
}
