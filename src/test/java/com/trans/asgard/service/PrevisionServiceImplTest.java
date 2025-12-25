package com.trans.asgard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trans.asgard.domain.historiquevente.repository.HistoriqueVenteRepository;
import com.trans.asgard.domain.prevision.dto.PrevisionResponse;
import com.trans.asgard.domain.prevision.model.Prevision;
import com.trans.asgard.domain.prevision.repository.PrevisionRepository;
import com.trans.asgard.domain.prevision.service.impl.PrevisionServiceImpl;
import com.trans.asgard.domain.stock.model.Stock;
import com.trans.asgard.domain.stock.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrevisionServiceImplTest {

    @Mock
    private PrevisionRepository previsionRepository;

    @Mock
    private HistoriqueVenteRepository historiqueVenteRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private WebClient webClient;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private PrevisionServiceImpl previsionService;

    private final Long PRODUCT_ID = 100L;
    private final Long ENTREPOT_ID = 5L;

    @BeforeEach
    void setUp() {
        // Reset any previous stubbing if needed
        reset(webClient);
    }

    @Nested
    @DisplayName("No sales history cases")
    class NoSalesHistoryTest {

        @Test
        @DisplayName("Should generate conservative forecast when no sales history")
        void shouldGenerateConservativeForecastWhenNoSalesHistory() {
            // Arrange
            when(historiqueVenteRepository.findByProductIdAndEntrepotIdAndDateVenteBetween(
                    anyLong(), anyLong(), any(), any()))
                    .thenReturn(Collections.emptyList());

            when(stockRepository.findByProductIdAndEntrepotId(PRODUCT_ID, ENTREPOT_ID))
                    .thenReturn(Optional.of(Stock.builder().quantity(200).build()));

            when(previsionRepository.save(any(Prevision.class)))
                    .thenAnswer(invocation -> {
                        Prevision p = invocation.getArgument(0);
                        p.setId(1L);
                        return p;
                    });

            // Act
            PrevisionResponse response = previsionService.generateAndSaveForecast(PRODUCT_ID, ENTREPOT_ID);

            // Assert
            assertThat(response.quantiteEstimee30Jours()).isGreaterThanOrEqualTo(50);
            assertThat(response.niveauConfiance()).isEqualTo(25);
            assertThat(response.detailsJson()).contains("no_data");
            assertThat(response.recommandation()).containsAnyOf("adequate", "No sales activity");
        }
    }
}