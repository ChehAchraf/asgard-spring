package com.trans.asgard.domain.product.service.interfaces;

import com.trans.asgard.domain.iam.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto create(ProductDto dto);
    ProductDto update(Long id,ProductDto dto);
    void delete(Long id);
    ProductDto getById(Long id);
    List<ProductDto> getAll();


}