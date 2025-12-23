package com.trans.asgard.domain.iam.controller;


import com.trans.asgard.domain.iam.dto.ProductDto;
import com.trans.asgard.domain.iam.dto.RegisterUserRequest;
import com.trans.asgard.domain.iam.dto.UserResponse;
import com.trans.asgard.domain.product.service.interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {


        private final ProductService productService;


        @GetMapping
        public ResponseEntity<List<ProductDto>> getAllProducts() {
            List<ProductDto> products = productService.getAll();
            return ResponseEntity.ok(products);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
            ProductDto product = productService.getById(id);
            return ResponseEntity.ok(product);
        }

        @PostMapping
        public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto dto) {
            ProductDto create = productService.create(dto);
            return new ResponseEntity<>(create, HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
            ProductDto updatedProduct = productService.update(id, dto);
            return ResponseEntity.ok(updatedProduct);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
            productService.delete(id);
            return ResponseEntity.noContent().build();
        }
    }