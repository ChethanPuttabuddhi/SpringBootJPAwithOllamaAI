package com.springboot.example.service;

import com.springboot.example.dto.RecommendationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.example.entity.Orders;
import com.springboot.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiRecommendationService {

    @Autowired
    OrderRepository orderRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public RecommendationResponse recommendForOrder(Long userId) {
        List<Orders> orders = orderRepository.findByUserId(userId);
        String prompt = buildPrompt(orders, String.valueOf(userId));

        try {
            String url = "http://localhost:11434/api/generate";

            Map<String, Object> request = new HashMap<>();
            request.put("model", "deepseek-r1:8b");
            request.put("prompt", prompt);
            request.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            // Parse the JSON response to extract the "response" field
            JsonNode jsonResponse = mapper.readTree(response.getBody());
            String aiText = jsonResponse.get("response").asText();

            System.out.println("AI Response: " + aiText);
            List<String> items = parseRecommendations(aiText);

            return new RecommendationResponse(items, aiText);
        } catch (Exception e) {
            // keep response simple on error
            return new RecommendationResponse(Collections.emptyList(), "AI call failed: " + e.getMessage());
        }
    }

    private String buildPrompt(List<Orders> orders, String userId) {
        // Build user's order history
        String userOrderSummary = (orders == null || orders.isEmpty())
            ? "No previous orders"
            : orders.stream()
                .map(o -> o.getProductName() + " (Amount: " + o.getOrderAmount() + ")")
                .collect(Collectors.joining(", "));

        // Get popular products across all users
        String popularProducts = getPopularProductsInfo();

        // Get co-purchase patterns (products frequently bought with user's products)
        String coPurchaseInfo = getCoPurchasePatterns(orders);

        // Get market trends
        String marketTrends = getMarketTrends();

        return "You are a product recommender AI assistant. You have access to:\n\n"
                + "=== CURRENT USER PROFILE ===\n"
                + "User ID: " + userId + "\n"
                + "User's Order History: " + userOrderSummary + "\n\n"
                + "=== MARKET INTELLIGENCE ===\n"
                + "Most Popular Products (across all users): " + popularProducts + "\n\n"
                + "Co-Purchase Patterns (products bought together): " + coPurchaseInfo + "\n\n"
                + "Market Trends: " + marketTrends + "\n\n"
                + "=== RECOMMENDATION TASK ===\n"
                + "Based on:\n"
                + "1. The user's personal purchase history\n"
                + "2. What similar users are buying\n"
                + "3. Products frequently purchased together\n"
                + "4. Current market trends\n\n"
                + "Recommend up to 2-3 next products the user is likely to buy next.\n"
                + "Return a short comma-separated list with each product followed by a one-line reason in parentheses.\n"
                + "Example: 'Product A (User bought similar items before), Product B (Trending with similar users)'";
    }

    /**
     * Gets the most popular/frequently ordered products across all users
     */
    private String getPopularProductsInfo() {
        try {
            List<Orders> allOrders = orderRepository.findAll();

            if (allOrders.isEmpty()) {
                return "No products data available";
            }

            // Count product frequency
            Map<String, Long> productCounts = allOrders.stream()
                    .filter(o -> o.getProductName() != null && !o.getProductName().isEmpty())
                    .collect(Collectors.groupingBy(
                            Orders::getProductName,
                            Collectors.counting()
                    ));

            // Get top 5 products
            return productCounts.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(5)
                    .map(e -> e.getKey() + " (ordered " + e.getValue() + " times)")
                    .collect(Collectors.joining(", "));

        } catch (Exception e) {
            return "Popular products: Data unavailable";
        }
    }

    /**
     * Identifies products frequently bought together with user's past purchases
     */
    private String getCoPurchasePatterns(List<Orders> userOrders) {
        try {
            if (userOrders == null || userOrders.isEmpty()) {
                return "No co-purchase patterns (user is new)";
            }

            // Get all products user has bought
            Set<String> userProducts = userOrders.stream()
                    .map(Orders::getProductName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Get all orders from other users
            List<Orders> allOrders = orderRepository.findAll();

            // Find products frequently bought by users who also bought user's products
            Map<String, Integer> coPurchaseCount = new HashMap<>();

            for (Orders order : allOrders) {
                String productName = order.getProductName();
                if (productName != null && !userProducts.contains(productName)) {
                    coPurchaseCount.merge(productName, 1, Integer::sum);
                }
            }

            // Return top co-purchased products
            return coPurchaseCount.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(3)
                    .map(e -> e.getKey() + " (co-purchased " + e.getValue() + " times)")
                    .collect(Collectors.joining(", "));

        } catch (Exception e) {
            return "Co-purchase data: Not available";
        }
    }

    /**
     * Analyzes market trends based on recent orders
     */
    private String getMarketTrends() {
        try {
            List<Orders> allOrders = orderRepository.findAll();

            if (allOrders.isEmpty()) {
                return "Insufficient data for trend analysis";
            }

            // Calculate average order value trend
            double avgOrderValue = allOrders.stream()
                    .mapToDouble(o -> o.getOrderAmount().doubleValue())
                    .average()
                    .orElse(0.0);

            // Get most recent products (last 10 orders)
            String recentTrends = allOrders.stream()
                    .sorted(Comparator.comparing(Orders::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(10)
                    .map(Orders::getProductName)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.joining(", "));

            return "Average order value: $" + String.format("%.2f", avgOrderValue)
                    + " | Recent trending products: " + (recentTrends.isEmpty() ? "None" : recentTrends);

        } catch (Exception e) {
            return "Market trends: Data unavailable";
        }
    }

    private String extractText(JsonNode response) {
        if (response == null) return "";
        JsonNode choices = response.get("choices");
        if (choices != null && choices.isArray() && !choices.isEmpty()) {
            JsonNode message = choices.get(0).get("message");
            if (message != null && message.get("content") != null) {
                return message.get("content").asText("");
            }
        }
        return "";
    }

    private List<String> parseRecommendations(String aiText) {
        if (aiText == null || aiText.isBlank()) return Collections.emptyList();

        // Try splitting by lines or commas, trim and dedupe
        String[] parts = aiText.split("\\r?\\n|,");
        return Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}