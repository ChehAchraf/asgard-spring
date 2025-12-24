package com.trans.asgard.domain.prevision.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trans.asgard.domain.prevision.dto.PrevisionResponse;
import com.trans.asgard.domain.prevision.dto.PrevisionTestRequest;
import com.trans.asgard.domain.prevision.model.Prevision;
import com.trans.asgard.domain.prevision.repository.PrevisionRepository;
import com.trans.asgard.domain.prevision.service.intefaces.PrevisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrevisionServiceImpl implements PrevisionService {

    private final PrevisionRepository previsionRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${ollama.api.url:http://asgardOllama:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:tinyllama:latest}")
    private String ollamaModel;

    @Override
    public PrevisionResponse generateAndSaveForecast(Long productId, Long warehouseId) {
        String historyJson = "[]";
        String productName = "Produit " + productId;
        int currentStock = 300;
        return generateAndSave(productId, warehouseId, historyJson, productName, currentStock);
    }

    @Override
    public PrevisionResponse generateAndSaveTestForecast(PrevisionTestRequest request) {
        return generateAndSave(
                request.produitId(),
                request.entrepotId(),
                request.historiqueVentesJson(),
                request.produitNom(),
                request.stockActuel()
        );
    }

    private PrevisionResponse generateAndSave(Long productId, Long warehouseId,
                                              String historyJson, String productName, int currentStock) {

        ForecastStats stats = calculateStats(historyJson, currentStock);

        String prompt = buildStructuredPrompt(historyJson, productName, currentStock, stats);
        String rawResponse = callOllama(prompt);

        Prevision prevision = parseAndBuildPrevision(productId, warehouseId, rawResponse, historyJson, stats);
        prevision = previsionRepository.save(prevision);

        return toResponse(prevision);
    }

    private String buildStructuredPrompt(String historyJson, String productName, int currentStock, ForecastStats stats) {
        return """
            You are a JSON-only inventory forecasting engine. Reply ONLY with valid JSON, no other text.
            
            Product: %s
            Current stock: %d units
            Historical sales: %s
            
            Statistical baseline calculated:
            - Average daily sales: %.1f units
            - 30-day forecast (baseline): %d units
            - Days of stock remaining: %d days
            - Data points: %d days
            - Trend: %s
            
            IMPORTANT STOCK ANALYSIS:
            - If days of stock > 30: Stock is ADEQUATE or OVERSTOCKED
            - If days of stock 20-30: Stock is ADEQUATE
            - If days of stock 10-20: Consider ordering soon
            - If days of stock < 10: Order urgently needed
            
            Current situation: %d units in stock = %d days of supply
            
            Your task: Analyze the data and adjust the forecast if you see clear patterns or trends.
            
            Return ONLY this JSON:
            {
              "estimatedQuantity30Days": <integer>,
              "confidenceLevel": <0-100 integer>
            }
            
            Rules:
            - estimatedQuantity30Days should be close to the baseline (%d) unless you see clear trends
            - If trend is increasing significantly, adjust forecast UP by 10-20%%
            - If trend is decreasing significantly, adjust forecast DOWN by 10-20%%
            - If trend is stable, use the baseline value
            - confidenceLevel: %d (based on %d data points)
            - DO NOT include "recommendation" field - it will be calculated separately
            
            JSON response:
            """.formatted(
                productName,
                currentStock,
                historyJson,
                stats.avgDaily,
                stats.forecast30Days,
                stats.daysOfStock,
                stats.dataPoints,
                stats.trend,
                currentStock,
                stats.daysOfStock,
                stats.forecast30Days,
                stats.suggestedConfidence,
                stats.dataPoints
        );
    }

    private String callOllama(String prompt) {
        try {
            String uri = ollamaBaseUrl + "/api/generate";
            String jsonPrompt = objectMapper.writeValueAsString(prompt);

            String requestBody = """
                    {
                      "model": "%s",
                      "prompt": %s,
                      "format": "json",
                      "stream": false,
                      "options": {
                        "temperature": 0.2,
                        "top_p": 0.9,
                        "top_k": 40,
                        "num_predict": 150
                      }
                    }
                    """.formatted(ollamaModel, jsonPrompt);

            log.debug("→ Calling Ollama: {} (model: {})", uri, ollamaModel);

            String response = webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            resp -> Mono.error(new RuntimeException("Ollama error: " + resp.statusCode())))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(90))
                    .blockOptional()
                    .orElse(null);

            if (response == null || response.isBlank()) {
                log.warn("Ollama returned empty response");
                return null;
            }

            log.debug("✓ Ollama response received");
            return response;

        } catch (Exception e) {
            log.error("Ollama call failed: {}", e.getMessage());
            return null;
        }
    }

    private Prevision parseAndBuildPrevision(Long productId, Long warehouseId,
                                             String rawResponse, String historyJson, ForecastStats stats) {

        int quantity = stats.forecast30Days;
        int confidence = stats.suggestedConfidence;

        if (rawResponse != null) {
            try {
                JsonNode root = objectMapper.readTree(rawResponse);
                String content = root.path("response").asText("").trim();

                if (!content.isEmpty()) {
                    JsonNode prediction = objectMapper.readTree(content);

                    quantity = getInt(prediction, "estimatedQuantity30Days", stats.forecast30Days);
                    confidence = getInt(prediction, "confidenceLevel", stats.suggestedConfidence);

                    quantity = Math.max(0, quantity);
                    confidence = Math.min(100, Math.max(0, confidence));

                    if (stats.dataPoints > 20) confidence = Math.min(100, confidence + 10);
                    if (stats.dataPoints > 50) confidence = Math.min(100, confidence + 15);

                    log.info("✓ LLM prediction: qty={}, conf={} (baseline: {})", quantity, confidence, stats.forecast30Days);
                }
            } catch (Exception e) {
                log.warn("Failed to parse LLM response: {}", e.getMessage());
            }
        } else {
            log.info("→ Using statistical baseline forecast");
        }

        String finalRecommendation = generateRecommendationFromForecast(
                quantity,
                stats.currentStock,
                stats.avgDaily
        );

        return Prevision.builder()
                .produitId(productId)
                .entrepotId(warehouseId)
                .datePrevision(LocalDate.now())
                .quantiteEstimee30Jours(quantity)
                .recommandation(finalRecommendation)
                .niveauConfiance(confidence)
                .detailsJson(String.format("%s | Forecast: %d | Baseline: %d | Data points: %d | %s",
                        rawResponse != null ? "LLM forecast" : "Statistical baseline",
                        quantity,
                        stats.forecast30Days,
                        stats.dataPoints,
                        truncate(String.valueOf(rawResponse), 300)))
                .build();
    }

    private ForecastStats calculateStats(String historyJson, int currentStock) {
        try {
            JsonNode array = objectMapper.readTree(historyJson);
            int dataPoints = array.size();

            if (dataPoints == 0) {
                int forecast = Math.max(50, currentStock / 10);
                return new ForecastStats(
                        dataPoints,
                        currentStock,
                        forecast / 30.0,
                        forecast,
                        1,
                        "No historical data",
                        "Order " + forecast + " units (conservative estimate)",
                        25
                );
            }

            long sum = 0;
            int validDays = 0;
            for (JsonNode day : array) {
                if (day.has("q")) {
                    sum += day.get("q").asLong();
                    validDays++;
                }
            }

            if (validDays == 0) {
                int forecast = Math.max(50, currentStock / 10);
                return new ForecastStats(0, currentStock, forecast / 30.0, forecast, 1, "No valid data",
                        "Order " + forecast + " units", 25);
            }

            double avgDaily = (double) sum / validDays;
            int forecast30Days = (int) Math.round(avgDaily * 30);
            forecast30Days = Math.max(10, forecast30Days);

            int daysOfStock = avgDaily > 0 ? (int) Math.round(currentStock / avgDaily) : 999;

            String trend = determineTrend(array, avgDaily);

            int confidence = calculateConfidence(validDays);

            String recommendation = generateRecommendation(forecast30Days, currentStock, avgDaily);

            return new ForecastStats(validDays, currentStock, avgDaily, forecast30Days, daysOfStock, trend, recommendation, confidence);

        } catch (Exception e) {
            log.error("Stats calculation failed", e);
            int fallback = Math.max(50, currentStock / 10);
            return new ForecastStats(0, currentStock, fallback / 30.0, fallback, 1, "Error",
                    "Order " + fallback + " units", 20);
        }
    }

    private String determineTrend(JsonNode array, double avgDaily) {
        try {
            if (array.size() < 3) return "Insufficient data";

            int midpoint = array.size() / 2;
            double firstHalfAvg = 0;
            double secondHalfAvg = 0;
            int firstCount = 0, secondCount = 0;

            for (int i = 0; i < array.size(); i++) {
                JsonNode day = array.get(i);
                if (day.has("q")) {
                    long qty = day.get("q").asLong();
                    if (i < midpoint) {
                        firstHalfAvg += qty;
                        firstCount++;
                    } else {
                        secondHalfAvg += qty;
                        secondCount++;
                    }
                }
            }

            if (firstCount == 0 || secondCount == 0) return "Stable";

            firstHalfAvg /= firstCount;
            secondHalfAvg /= secondCount;

            double change = ((secondHalfAvg - firstHalfAvg) / firstHalfAvg) * 100;

            if (change > 15) return "Increasing (+15%+)";
            if (change < -15) return "Decreasing (-15%+)";
            return "Stable";

        } catch (Exception e) {
            return "Unknown";
        }
    }

    private int calculateConfidence(int dataPoints) {
        if (dataPoints >= 60) return 85;
        if (dataPoints >= 30) return 75;
        if (dataPoints >= 14) return 65;
        if (dataPoints >= 7) return 55;
        if (dataPoints >= 3) return 45;
        return 30;
    }

    private String generateRecommendation(int forecast30Days, int currentStock, double avgDaily) {
        if (avgDaily <= 0) {
            return "No sales activity detected";
        }

        int daysOfStock = (int) Math.round(currentStock / avgDaily);
        int needed = forecast30Days - currentStock;

        if (needed > 100) {
            return String.format("Order %d units (%.0f days left)", needed, (double) daysOfStock);
        } else if (needed > 0) {
            return String.format("Order %d units soon", needed);
        } else if (daysOfStock > 45) {
            return String.format("Overstocked: %.0f days supply", (double) daysOfStock);
        } else {
            return String.format("Stock adequate: %.0f days left", (double) daysOfStock);
        }
    }

    // New method to generate recommendation from the final forecast
    private String generateRecommendationFromForecast(int forecast30Days, int currentStock, double avgDaily) {
        if (avgDaily <= 0) {
            return "No sales activity detected";
        }

        int daysOfStock = (int) Math.round(currentStock / avgDaily);
        int needed = forecast30Days - currentStock;

        if (needed > 100) {
            return String.format("Order %d units urgently (%d days left)", needed, daysOfStock);
        } else if (needed > 0) {
            return String.format("Order %d units soon (%d days left)", needed, daysOfStock);
        } else if (daysOfStock > 45) {
            return String.format("Overstocked: %d days supply", daysOfStock);
        } else {
            return String.format("Stock adequate: %d days left", daysOfStock);
        }
    }

    private record ForecastStats(
            int dataPoints,
            int currentStock,
            double avgDaily,
            int forecast30Days,
            int daysOfStock,
            String trend,
            String recommendation,
            int suggestedConfidence
    ) {}

    private String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    private int getInt(JsonNode node, String field, int defaultValue) {
        return Optional.ofNullable(node.get(field))
                .filter(n -> !n.isNull() && n.isNumber())
                .map(JsonNode::asInt)
                .orElse(defaultValue);
    }

    private String getString(JsonNode node, String field, String defaultValue) {
        return Optional.ofNullable(node.get(field))
                .filter(n -> !n.isNull() && n.isTextual())
                .map(JsonNode::asText)
                .orElse(defaultValue);
    }

    private PrevisionResponse toResponse(Prevision p) {
        return new PrevisionResponse(
                p.getId(),
                p.getProduitId(),
                p.getEntrepotId(),
                p.getDatePrevision(),
                p.getQuantiteEstimee30Jours(),
                p.getRecommandation(),
                p.getNiveauConfiance(),
                p.getDetailsJson()
        );
    }
}