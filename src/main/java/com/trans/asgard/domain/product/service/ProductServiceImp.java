package com.trans.asgard.domain.product.service;

import com.trans.asgard.domain.product.dto.ProductDto;
import com.trans.asgard.domain.product.mapper.ProductMapper;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.product.repository.ProductRepository;
import com.trans.asgard.domain.product.service.interfaces.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private  final ProductMapper mapper;

    @Override
    public ProductDto create(ProductDto dto)
    {
        Product p =mapper.toEntity(dto);
        return mapper.toDto(productRepository.save(p));

    }


    @Override
    public ProductDto update(Long id,ProductDto dto)
    {
        Product p = productRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found with id " + id));

        p.setCategorie(dto.getCategorie());
        p.setNom(dto.getNom());
        p.setDescription(dto.getDescription());
        p.setPoids(dto.getPoids());
        p.setMarge(dto.getMarge());
        p.setPrixVente(dto.getPrixVente());

        return mapper.toDto(productRepository.save(p));


    }

    @Override
    public void delete(Long id)
    {
        Product p =productRepository.findById(id)
                .orElseThrow((()-> new RuntimeException("Product not found with id " + id)));

        productRepository.delete(p);

    }


    @Override
    public ProductDto getById(Long id)
    {
        Product p =productRepository.findById(id)
                .orElseThrow((()-> new RuntimeException("Product not found with id " + id)));

        return mapper.toDto(p);
    }
    @Override
    public List<ProductDto> getAll()
    {
        return  productRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }



}

