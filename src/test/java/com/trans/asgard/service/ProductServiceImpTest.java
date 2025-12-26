package com.trans.asgard.service;

import com.trans.asgard.domain.product.dto.ProductDto;
import com.trans.asgard.domain.product.mapper.ProductMapper;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.product.repository.ProductRepository;
import com.trans.asgard.domain.product.service.ProductServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImp productService;

    private Product productEntity;
    private ProductDto productDto;
    private ProductDto createInputDto;

    @BeforeEach
    void setUp() {
        productEntity = Product.builder()
                .id(1L)
                .nom("Coca-Cola 1.5L")
                .description("Boisson gazeuse sucrée")
                .categorie("Boissons")
                .prixVente(2.50)
                .prixAchat(1.80)
                .marge(0.70)
                .poids(1.5)
                .unite("L")
                .build();

        productDto = new ProductDto(
                1L,
                "Coca-Cola 1.5L",
                "Boisson gazeuse sucrée",
                "Boissons",
                2.50,
                1.80,
                0.70,
                1.5,
                "L",
                null,
                null
        );

        createInputDto = new ProductDto(
                null, // id sera généré
                "Coca-Cola 1.5L",
                "Boisson gazeuse sucrée",
                "Boissons",
                2.50,
                1.80,
                0.70,
                1.5,
                "L",
                null,
                null
        );
    }

    @Nested
    @DisplayName("create")
    class CreateTest {

        @Test
        @DisplayName("Devrait créer un produit avec succès")
        void shouldCreateProductSuccessfully() {
            // Arrange
            when(mapper.toEntity(createInputDto)).thenReturn(productEntity);
            when(productRepository.save(any(Product.class))).thenReturn(productEntity);
            when(mapper.toDto(productEntity)).thenReturn(productDto);

            // Act
            ProductDto result = productService.create(createInputDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNom()).isEqualTo("Coca-Cola 1.5L");
            assertThat(result.getPrixVente()).isEqualTo(2.50);
            assertThat(result.getMarge()).isEqualTo(0.70);

            verify(mapper).toEntity(createInputDto);
            verify(productRepository).save(any(Product.class));
            verify(mapper).toDto(productEntity);
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTest {

        @Test
        @DisplayName("Devrait mettre à jour un produit existant")
        void shouldUpdateExistingProduct() {
            // Arrange
            ProductDto updateDto = new ProductDto(
                    1L,
                    "Coca-Cola 2L - NOUVEAU",
                    "Nouvelle version 2 litres",
                    "Boissons",
                    3.20,
                    2.10,
                    1.10,
                    2.0,
                    "L",
                    null,
                    null
            );

            Product updatedEntity = Product.builder()
                    .id(1L)
                    .nom("Coca-Cola 2L - NOUVEAU")
                    .description("Nouvelle version 2 litres")
                    .categorie("Boissons")
                    .prixVente(3.20)
                    .prixAchat(2.10)
                    .marge(1.10)
                    .poids(2.0)
                    .unite("L")
                    .build();

            ProductDto updatedDto = new ProductDto(
                    1L,
                    "Coca-Cola 2L - NOUVEAU",
                    "Nouvelle version 2 litres",
                    "Boissons",
                    3.20,
                    2.10,
                    1.10,
                    2.0,
                    "L",
                    null,
                    null
            );

            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(productRepository.save(any(Product.class))).thenReturn(updatedEntity);
            when(mapper.toDto(updatedEntity)).thenReturn(updatedDto);

            // Act
            ProductDto result = productService.update(1L, updateDto);

            // Assert
            assertThat(result.getNom()).isEqualTo("Coca-Cola 2L - NOUVEAU");
            assertThat(result.getPrixVente()).isEqualTo(3.20);
            assertThat(result.getPoids()).isEqualTo(2.0);

            verify(productRepository).findById(1L);
            verify(productRepository).save(any(Product.class));
            verify(mapper).toDto(updatedEntity);
        }

        @Test
        @DisplayName("Devrait lever une exception si le produit n'existe pas")
        void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            ProductDto dummyDto = new ProductDto(null, "Test", "Test", "Test", 10.0, 5.0, 5.0, 1.0, "kg",null,null);

            assertThatThrownBy(() -> productService.update(999L, dummyDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Product not found with id 999");

            verify(productRepository).findById(999L);
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("Devrait supprimer un produit existant")
        void shouldDeleteExistingProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

            productService.delete(1L);

            verify(productRepository).findById(1L);
            verify(productRepository).delete(productEntity);
        }

        @Test
        @DisplayName("Devrait lever une exception si le produit n'existe pas")
        void shouldThrowExceptionWhenDeletingNonExistingProduct() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.delete(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Product not found with id 999");
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTest {

        @Test
        @DisplayName("Devrait retourner un produit existant")
        void shouldReturnExistingProduct() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
            when(mapper.toDto(productEntity)).thenReturn(productDto);

            ProductDto result = productService.getById(1L);

            assertThat(result).usingRecursiveComparison().isEqualTo(productDto);
        }

        @Test
        @DisplayName("Devrait lever une exception pour un produit inexistant")
        void shouldThrowExceptionForNonExistingId() {
            when(productRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getById(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Product not found with id 999");
        }
    }

    @Nested
    @DisplayName("getAll")
    class GetAllTest {

        @Test
        @DisplayName("Devrait retourner tous les produits")
        void shouldReturnAllProducts() {
            List<Product> products = List.of(productEntity);
            when(productRepository.findAll()).thenReturn(products);
            when(mapper.toDto(productEntity)).thenReturn(productDto);

            List<ProductDto> result = productService.getAll();

            assertThat(result)
                    .hasSize(1)
                    .containsExactly(productDto);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide s'il n'y a aucun produit")
        void shouldReturnEmptyListWhenNoProducts() {
            when(productRepository.findAll()).thenReturn(List.of());

            List<ProductDto> result = productService.getAll();

            assertThat(result).isEmpty();
        }
    }
}