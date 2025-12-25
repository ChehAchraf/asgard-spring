package com.trans.asgard.domain.prevision.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trans.asgard.domain.historiquevente.model.HistoriqueVente;
import com.trans.asgard.domain.historiquevente.repository.HistoriqueVenteRepository;
import com.trans.asgard.domain.prevision.dto.PrevisionResponse;
import com.trans.asgard.domain.prevision.model.Prevision;
import com.trans.asgard.domain.prevision.repository.PrevisionRepository;
import com.trans.asgard.domain.prevision.service.intefaces.PrevisionService;
import com.trans.asgard.domain.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrevisionServiceImpl implements PrevisionService {

    private final PrevisionRepository previsionRepository;
    private final HistoriqueVenteRepository historiqueVenteRepository;
    private final StockRepository stockRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ollama.api.url:http://asgardOllama:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:tinyllama:latest}")
    private String ollamaModel;

    @Override
    public PrevisionResponse generateAndSaveForecast(Long productId, Long warehouseId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(90);

        List<HistoriqueVente> salesHistory = historiqueVenteRepository
                .findByProductIdAndEntrepotIdAndDateVenteBetween(productId, warehouseId, startDate, endDate);

        int currentStock = stockRepository
                .findByProductIdAndEntrepotId(productId, warehouseId)
                .map(stock -> stock.getQuantity())
                .orElse(0);

        String productName = salesHistory.isEmpty()
                ? "Produit " + productId
                : salesHistory.get(0).getProduct().getNom();

        return generateForecast(productId, warehouseId, salesHistory, productName, currentStock);
    }

    private PrevisionResponse generateForecast(Long productId, Long warehouseId,
                                               List<HistoriqueVente> salesHistory,
                                               String productName, int currentStock) {
        Stats stats = calculateEnhancedStats(salesHistory, currentStock);

        Integer llmForecast = tryLlmEnhancement(stats, productName, currentStock);

        int finalForecast = (llmForecast != null && isReasonable(llmForecast, stats))
                ? llmForecast
                : stats.forecast30Days;

        String recommendation = generateRecommendation(finalForecast, currentStock, stats.avgDaily);

        Prevision prevision = Prevision.builder()
                .produitId(productId)
                .entrepotId(warehouseId)
                .datePrevision(LocalDate.now())
                .quantiteEstimee30Jours(finalForecast)
                .recommandation(recommendation)
                .niveauConfiance(stats.confidence)
                .detailsJson(buildDetailsJson(stats, llmForecast))
                .build();

        prevision = previsionRepository.save(prevision);
        return toResponse(prevision);
    }

    private Stats calculateEnhancedStats(List<HistoriqueVente> salesHistory, int currentStock) {
        if (salesHistory.isEmpty()) {
            int conservativeForecast = Math.max(50, currentStock / 10);
            return new Stats(0, 0, conservativeForecast, 0, conservativeForecast, 25, "no_data");
        }

        double[] dailySales = salesHistory.stream()
                .mapToDouble(HistoriqueVente::getQuantiteVendue)
                .toArray();

        int n = dailySales.length;

        double sum = 0, sumSq = 0;
        for (double sale : dailySales) {
            sum += sale;
            sumSq += sale * sale;
        }

        double mean = sum / n;
        double variance = (sumSq / n) - (mean * mean);
        double stdDev = Math.sqrt(Math.max(0, variance));

        double cv = mean > 0 ? stdDev / mean : 1.0;

        double wma = calculateWeightedMovingAverage(dailySales);

        double trendSlope = calculateTrendSlope(dailySales);
        double trendStrength = Math.abs(trendSlope) / (mean + 1);

        double forecastDaily = wma + (trendSlope * 15);
        forecastDaily = Math.max(0, forecastDaily);
        int forecast30Days = (int) Math.round(forecastDaily * 30);
        forecast30Days = Math.max(10, forecast30Days);

        int confidence = calculateEnhancedConfidence(n, cv, trendStrength);

        int daysOfStock = mean > 0 ? (int) Math.round(currentStock / mean) : 999;

        String trendLabel = trendSlope > 0.5 ? "increasing" :
                trendSlope < -0.5 ? "decreasing" : "stable";

        return new Stats(n, mean, forecast30Days, daysOfStock, currentStock, confidence, trendLabel);
    }

    private double calculateWeightedMovingAverage(double[] sales) {
        int n = sales.length;
        double weightSum = 0, valueSum = 0;

        for (int i = 0; i < n; i++) {
            double weight = i + 1;
            weightSum += weight;
            valueSum += sales[i] * weight;
        }

        return weightSum > 0 ? valueSum / weightSum : 0;
    }

    private double calculateTrendSlope(double[] sales) {
        int n = sales.length;
        if (n < 3) return 0;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += sales[i];
            sumXY += i * sales[i];
            sumX2 += i * i;
        }

        double denominator = n * sumX2 - sumX * sumX;
        if (Math.abs(denominator) < 0.001) return 0;

        return (n * sumXY - sumX * sumY) / denominator;
    }

    private int calculateEnhancedConfidence(int dataPoints, double cv, double trendStrength) {
        int baseConfidence;
        if (dataPoints >= 60) baseConfidence = 90;
        else if (dataPoints >= 30) baseConfidence = 80;
        else if (dataPoints >= 14) baseConfidence = 70;
        else if (dataPoints >= 7) baseConfidence = 55;
        else baseConfidence = 40;

        if (cv > 1.0) baseConfidence -= 20;
        else if (cv > 0.5) baseConfidence -= 10;

        if (trendStrength > 0.3) baseConfidence -= 10;
        else if (trendStrength > 0.1) baseConfidence -= 5;

        return Math.max(25, Math.min(95, baseConfidence));
    }

    private Integer tryLlmEnhancement(Stats stats, String productName, int currentStock) {
        try {
            String prompt = buildCompactPrompt(stats, productName, currentStock);
            String response = callOllama(prompt);

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);
                String content = root.path("response").asText("").trim();
                if (!content.isEmpty()) {
                    JsonNode prediction = objectMapper.readTree(content);
                    return prediction.path("estimatedQuantity30Days").asInt(0);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private String buildCompactPrompt(Stats stats, String productName, int currentStock) {
        return String.format("""
            Forecast JSON only. Product: %s, Stock: %d
            Stats: avg=%.1f, 30d_baseline=%d, trend=%s, days_left=%d, points=%d
            Adjust baseline if strong trend detected. Return:
            {"estimatedQuantity30Days": <int>, "confidenceLevel": %d}
            """,
                productName, currentStock, stats.avgDaily, stats.forecast30Days,
                stats.trendLabel, stats.daysOfStock, stats.dataPoints, stats.confidence
        );
    }

    private String callOllama(String prompt) {
        try {
            String requestBody = String.format("""
                {"model":"%s","prompt":%s,"format":"json","stream":false,
                "options":{"temperature":0.2,"num_predict":100}}
                """,
                    ollamaModel,
                    objectMapper.writeValueAsString(prompt)
            );

            return webClient.post()
                    .uri(ollamaBaseUrl + "/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .blockOptional()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isReasonable(int llmForecast, Stats stats) {
        double ratio = (double) llmForecast / stats.forecast30Days;
        return ratio >= 0.5 && ratio <= 1.5 && llmForecast >= 10;
    }

    private String generateRecommendation(int forecast, int currentStock, double avgDaily) {
        if (avgDaily <= 0) return "No sales activity detected";

        int daysOfStock = (int) Math.round(currentStock / avgDaily);
        int needed = forecast - currentStock;

        if (needed > 100) return String.format("Order %d units urgently (%d days left)", needed, daysOfStock);
        if (needed > 0) return String.format("Order %d units soon (%d days left)", needed, daysOfStock);
        if (daysOfStock > 45) return String.format("Overstocked: %d days supply", daysOfStock);
        return String.format("Stock adequate: %d days left", daysOfStock);
    }

    private String buildDetailsJson(Stats stats, Integer llmForecast) {
        return String.format("Method: %s | Forecast: %d | DataPoints: %d | Confidence: %d%% | Trend: %s",
                llmForecast != null ? "LLM+Stats" : "Statistics",
                stats.forecast30Days,
                stats.dataPoints,
                stats.confidence,
                stats.trendLabel
        );
    }

    private PrevisionResponse toResponse(Prevision p) {
        return new PrevisionResponse(
                p.getId(), p.getProduitId(), p.getEntrepotId(), p.getDatePrevision(),
                p.getQuantiteEstimee30Jours(), p.getRecommandation(),
                p.getNiveauConfiance(), p.getDetailsJson()
        );
    }

    private record Stats(
            int dataPoints, double avgDaily, int forecast30Days,
            int daysOfStock, int currentStock, int confidence, String trendLabel
    ) {}
}