package com.trans.asgard.domain.product.service;

import com.trans.asgard.domain.product.dto.ProductDto;
import com.trans.asgard.domain.product.mapper.ProductMapper;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.product.repository.ProductRepository;
import com.trans.asgard.domain.stock.service.interfaces.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductServiceImp productService;

    @Test
    void create() {
        ProductDto requestDto = new ProductDto();
        requestDto.setNom("PC Gamer");
        requestDto.setInitialEntrepotId(1L);
        requestDto.setInitialQuantity(10);

        Product productEntity = Product.builder().id(100L).nom("PC Gamer").build();
        ProductDto responseDto = new ProductDto();
        responseDto.setNom("PC Gamer");

        when(productMapper.toEntity(requestDto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toDto(productEntity)).thenReturn(responseDto);

        ProductDto result = productService.create(requestDto);

        assertNotNull(result);
        assertEquals("PC Gamer", result.getNom());

        verify(stockService, times(1)).addProduct(100L, 1L, 10);
    }

    @Test
    void create_WithoutInitialStock_ShouldOnlySaveProduct() {
        ProductDto requestDto = new ProductDto();
        requestDto.setNom("Mouse");
        requestDto.setInitialEntrepotId(null);
        requestDto.setInitialQuantity(null);

        Product productEntity = Product.builder().id(200L).nom("Mouse").build();
        ProductDto responseDto = new ProductDto();
        responseDto.setNom("Mouse");

        when(productMapper.toEntity(requestDto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toDto(productEntity)).thenReturn(responseDto);

        ProductDto result = productService.create(requestDto);

        assertNotNull(result);

        verify(stockService, never()).addProduct(anyLong(), anyLong(), anyInt());
    }
}