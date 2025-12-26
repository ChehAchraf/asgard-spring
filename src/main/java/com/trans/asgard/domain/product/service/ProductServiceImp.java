package com.trans.asgard.domain.product.service;

import com.trans.asgard.domain.product.dto.ProductDto;
import com.trans.asgard.domain.product.mapper.ProductMapper;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.product.repository.ProductRepository;
import com.trans.asgard.domain.product.service.interfaces.ProductService;
import com.trans.asgard.domain.stock.service.interfaces.StockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final StockService stockService;

    @Transactional
    @Override
    public ProductDto create(ProductDto dto) {
        Product p = mapper.toEntity(dto);
        Product savedProduct = productRepository.save(p);

        boolean hasQty = dto.getInitialQuantity() != null;
        boolean hasEntrepot = dto.getInitialEntrepotId() != null;

        if (hasQty && !hasEntrepot) {
            throw new IllegalArgumentException("error : which entrepot you wanna save this product in ?");
        }

        if (!hasQty && hasEntrepot) {
            throw new IllegalArgumentException("error : how many product you wanna save ? ");
        }

        if (hasQty && hasEntrepot) {
            stockService.addProduct(
                    savedProduct.getId(),
                    dto.getInitialEntrepotId(),
                    dto.getInitialQuantity()
            );
        }


        return mapper.toDto(savedProduct);
    }


    @Override
    public ProductDto update(Long id, ProductDto dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        p.setCategorie(dto.getCategorie());
        p.setNom(dto.getNom());
        p.setDescription(dto.getDescription());
        p.setPoids(dto.getPoids());
        p.setMarge(dto.getMarge());
        p.setPrixVente(dto.getPrixVente());

        return mapper.toDto(productRepository.save(p));


    }

    @Override
    public void delete(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow((() -> new RuntimeException("Product not found with id " + id)));

        productRepository.delete(p);

    }


    @Override
    public ProductDto getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow((() -> new RuntimeException("Product not found with id " + id)));

        return mapper.toDto(p);
    }

    @Override
    public List<ProductDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }


}

