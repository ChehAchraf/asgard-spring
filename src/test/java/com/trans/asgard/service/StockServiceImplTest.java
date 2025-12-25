package com.trans.asgard.service;

import com.trans.asgard.domain.Entrepot.model.Entrepot;
import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import com.trans.asgard.domain.historiquevente.repository.HistoriqueVenteRepository;
import com.trans.asgard.domain.product.model.Product;
import com.trans.asgard.domain.stock.model.Stock;
import com.trans.asgard.domain.stock.repository.StockRepository;
import com.trans.asgard.domain.stock.service.StockServiceImpl;
import com.trans.asgard.infrastructure.exception.custom.ResourceNotFoundException;
import com.trans.asgard.infrastructure.exception.custom.StockInsufficientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private HistoriqueVenteRepository historiqueVenteRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Captor
    private ArgumentCaptor<Stock> stockCaptor;

    @Captor
    private ArgumentCaptor<HistoriqueVente> historiqueCaptor;

    private Stock stock;
    private Product product;
    private Entrepot entrepot;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(100L)
                .nom("Coca-Cola 1.5L")
                .build();

        entrepot = Entrepot.builder()
                .id(5L)
                .nom("Entrepôt Lyon")
                .build();

        stock = Stock.builder()
                .id(42L)
                .quantity(150)
                .alertThreshold(20)
                .product(product)
                .entrepot(entrepot)
                .build();
    }

    @Nested
    @DisplayName("sellProduct")
    class SellProductTest {

        @Test
        @DisplayName("Devrait vendre avec succès quand le stock est suffisant")
        void shouldSellSuccessfullyWhenStockIsSufficient() {
            // Arrange
            int quantityToSell = 30;
            when(stockRepository.findById(42L)).thenReturn(Optional.of(stock));

            // Act
            stockService.sellProduct(42L, quantityToSell);

            // Assert
            verify(stockRepository).findById(42L);

            // Vérification de la mise à jour du stock
            verify(stockRepository).save(stockCaptor.capture());
            Stock savedStock = stockCaptor.getValue();
            assertThat(savedStock.getQuantity()).isEqualTo(150 - 30);

            // Vérification de la création de l'historique
            verify(historiqueVenteRepository).save(historiqueCaptor.capture());
            HistoriqueVente history = historiqueCaptor.getValue();

            assertThat(history.getQuantiteVendue()).isEqualTo(quantityToSell);
            assertThat(history.getProduct()).isSameAs(product);
            assertThat(history.getEntrepot()).isSameAs(entrepot);
            assertThat(history.getDateVente()).isEqualTo(LocalDate.now());
            assertThat(history.getJourSemaine()).isEqualTo(LocalDate.now().getDayOfWeek().toString());
            assertThat(history.getMois()).isEqualTo(LocalDate.now().getMonthValue());
            assertThat(history.getAnnee()).isEqualTo(LocalDate.now().getYear());

            verifyNoMoreInteractions(stockRepository, historiqueVenteRepository);
        }

        @Test
        @DisplayName("Devrait lever ResourceNotFoundException quand le stock n'existe pas")
        void shouldThrowResourceNotFoundWhenStockDoesNotExist() {
            // Arrange
            when(stockRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> stockService.sellProduct(999L, 10))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Stock not found");

            verify(stockRepository).findById(999L);
            verifyNoInteractions(historiqueVenteRepository);
            verifyNoMoreInteractions(stockRepository);
        }

        @Test
        @DisplayName("Devrait lever StockInsufficientException quand la quantité est insuffisante")
        void shouldThrowStockInsufficientWhenQuantityIsNotEnough() {
            // Arrange
            int quantityToSell = 200; // > 150
            when(stockRepository.findById(42L)).thenReturn(Optional.of(stock));

            // Act & Assert
            assertThatThrownBy(() -> stockService.sellProduct(42L, quantityToSell))
                    .isInstanceOf(StockInsufficientException.class)
                    .hasMessageContaining("Stock is not okay");

            // Vérifie qu'aucune modification n'a été enregistrée
            verify(stockRepository).findById(42L);
            verifyNoInteractions(historiqueVenteRepository);
            verify(stockRepository, never()).save(any(Stock.class));
        }

        @Test
        @DisplayName("Devrait gérer correctement la vente de quantité exacte au stock")
        void shouldAllowSellingExactAvailableQuantity() {
            // Arrange
            int quantityToSell = 150;
            when(stockRepository.findById(42L)).thenReturn(Optional.of(stock));

            // Act
            stockService.sellProduct(42L, quantityToSell);

            // Assert
            verify(stockRepository).save(stockCaptor.capture());
            assertThat(stockCaptor.getValue().getQuantity()).isZero();

            verify(historiqueVenteRepository).save(any(HistoriqueVente.class));
        }
    }
}