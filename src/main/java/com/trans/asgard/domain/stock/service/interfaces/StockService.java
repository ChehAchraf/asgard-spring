package com.trans.asgard.domain.stock.service.interfaces;

public interface StockService {
    void sellProduct(Long stockId, int quantity);
    void addProduct(Long productId, Long entrepotId, int quantityAdded);
}
