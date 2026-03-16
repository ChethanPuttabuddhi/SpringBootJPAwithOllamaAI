# AI Recommendation Service - Usage Guide & Examples

## 1. Controller Endpoint

### RecommendationController
```java
@RestController
@RequestMapping("/api")
public class RecommendationController {

    @Autowired
    private AiRecommendationService aiRecommendationService;

    /**
     * Get AI-powered product recommendations for a specific user
     * 
     * @param userId The ID of the user to get recommendations for
     * @return RecommendationResponse containing recommended products
     */
    @GetMapping("/recommendations")
    public RecommendationResponse getRecommendations(
            @RequestParam(required = true) Long userId) {
        return aiRecommendationService.recommendForOrder(userId);
    }
}
```

---

## 2. API Usage Examples

### Example 1: New User (No Order History)
```bash
GET http://localhost:8080/api/recommendations?userId=999

Response:
{
  "recommendations": [
    "iPhone Pro (Most popular with first-time buyers)",
    "USB-C Cable Bundle (Accessory everyone needs)",
    "Phone Case (Trending protection item)"
  ],
  "raw": "AI response text..."
}
```

### Example 2: Frequent Apple Buyer
```bash
GET http://localhost:8080/api/recommendations?userId=5

User's History: iPhone, MacBook, iPad
Database Data:
  - Popular: iPhone (20x), MacBook (15x), AirPods (12x)
  - Co-purchase: AppleCare (18x), AirPods (14x), USB-C Hub (10x)
  - Trending: Apple Watch, AirPods Pro, MacBook Accessories

Response:
{
  "recommendations": [
    "AirPods Pro (You've bought iPhones and MacBooks before)",
    "AppleCare+ (Co-purchased with 18 MacBook buyers)",
    "Apple Watch Series 9 (Trending with tech enthusiasts)"
  ],
  "raw": "AI response text..."
}
```

### Example 3: Budget Buyer
```bash
GET http://localhost:8080/api/recommendations?userId=42

User's History: Basic Phone, Basic Headphones
Database Data:
  - Popular: Basic Phone (35x), Basic Headphones (28x), Charger (25x)
  - Co-purchase: Screen Protector (30x), Phone Case (28x), Charger (20x)
  - Trending: Budget Accessories, Phone Protection

Response:
{
  "recommendations": [
    "Screen Protector Bundle (28 budget buyers added this to cart)",
    "Fast Charger (Trending accessory this month)",
    "Phone Stand (Popular with budget phone buyers)"
  ],
  "raw": "AI response text..."
}
```

---

## 3. Testing the Endpoint

### Using cURL
```bash
# Test with user ID 5
curl -X GET "http://localhost:8080/api/recommendations?userId=5" \
     -H "Content-Type: application/json"

# Pretty print response
curl -X GET "http://localhost:8080/api/recommendations?userId=5" 2>/dev/null | jq .
```

### Using Postman
```
Method: GET
URL: http://localhost:8080/api/recommendations?userId=5
Headers: Content-Type: application/json
```

### Using Spring Boot Test
```java
@SpringBootTest
@AutoConfigureMockMvc
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetRecommendations() throws Exception {
        mockMvc.perform(get("/api/recommendations?userId=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations").isArray())
                .andExpect(jsonPath("$.recommendations.length()").value(2))
                .andDo(print());
    }

    @Test
    public void testRecommendationsContainReasons() throws Exception {
        mockMvc.perform(get("/api/recommendations?userId=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations[*]").isArray())
                .andExpect(content().string(containsString("("))); // Has reason in parens
    }
}
```

---

## 4. Response Format

### RecommendationResponse Class
```java
public class RecommendationResponse {
    private List<String> recommendations;  // List of recommended products
    private String raw;                    // Full AI response for debugging

    // Getters/Setters
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    
    public String getRaw() { return raw; }
    public void setRaw(String raw) { this.raw = raw; }
}
```

### Example Response
```json
{
  "recommendations": [
    "AirPods Pro (Complements your iPhone purchase)",
    "Apple Watch Series 9 (Trending with iPhone users)",
    "AppleCare+ (30 iPhone owners bought this)"
  ],
  "raw": "Based on your purchase history of iPhone and MacBook, and the market data showing that iPhone buyers frequently purchase AirPods and AppleCare, I recommend: 1. AirPods Pro - These are the most popular accessory purchased by iPhone users, offering seamless integration. 2. Apple Watch Series 9 - Trending significantly this month with a 45% increase in sales among tech-focused users. 3. AppleCare+ - 30 users who bought similar products to you added this protection plan."
}
```

---

## 5. Service Method Details

### AiRecommendationService.recommendForOrder()

```java
public RecommendationResponse recommendForOrder(Long userId) {
    // Step 1: Get user's order history
    List<Orders> orders = orderRepository.findByUserId(userId);
    
    // Step 2: Build comprehensive prompt with market data
    String prompt = buildPrompt(orders, String.valueOf(userId));
    
    // Step 3: Call OpenAI API
    // - Sends prompt with all intelligence
    // - Receives AI-generated recommendations
    
    // Step 4: Parse and return recommendations
    return new RecommendationResponse(items, aiText);
}
```

---

## 6. Data Sources Used by AI

The prompt sent to OpenAI includes:

```
=== CURRENT USER PROFILE ===
User ID: 5
User's Order History: iPhone ($999), MacBook ($1299), iPad ($599)

=== MARKET INTELLIGENCE ===

Most Popular Products (across all users):
- iPhone (20 orders)
- MacBook (15 orders)
- AirPods (12 orders)
- USB-C Hub (8 orders)
- AppleCare (7 orders)

Co-Purchase Patterns (products bought together):
- AppleCare (bought 18 times with similar products)
- AirPods (bought 14 times with similar products)
- USB-C Cable (bought 10 times with similar products)

Market Trends:
- Average order value: $1,100
- Trending products: Apple Watch, iPad Pro, MacBook Pro

=== RECOMMENDATION TASK ===
Based on all above factors, recommend 2-3 next products...
```

---

## 7. Error Handling

### When OpenAI API Call Fails
```json
{
  "recommendations": [],
  "raw": "AI call failed: Connection timeout"
}
```

### When User Has No Order History
```
User's Order History: No previous orders

AI might recommend:
- "iPhone (Most popular product overall)"
- "MacBook (Trending with first-time buyers)"
```

---

## 8. Real-World Scenarios

### Scenario 1: E-Commerce Platform
```
User: John (ID: 1)
History: Running Shoes ($120), Sports Watch ($250), Gym Bag ($80)

AI Recommendations:
1. "Running Socks Bundle (Runners buy this 22 times after shoes)"
2. "Wireless Headphones (Trending with active users)"
3. "Running Shorts (Co-purchased with 18 running shoe buyers)"
```

### Scenario 2: Electronics Store
```
User: Tech Enthusiast (ID: 2)
History: iPhone 15 Pro ($1,200), Apple Watch ($400), iPad Pro ($1,100)

AI Recommendations:
1. "AirPods Max (Next step for Apple ecosystem users)"
2. "Magic Keyboard (18 iPad Pro buyers also bought this)"
3. "HomePod Mini (Trending smart home product)"
```

### Scenario 3: New Customer
```
User: New User (ID: 999)
History: (No orders yet)

AI Recommendations:
1. "Best Seller Bundle (Most popular starting point)"
2. "New Customer Discount Pack (Trending with new users)"
3. "Essential Accessories (35 first-time buyers purchased)"
```

---

## 9. Performance Tips

### Caching Recommendations
```java
@Service
public class AiRecommendationService {
    
    @Cacheable(value = "recommendations", key = "#userId")
    public RecommendationResponse recommendForOrder(Long userId) {
        // AI call (cached for 1 hour)
        return aiRecommendationService.recommendForOrder(userId);
    }
}
```

### Batch Process Recommendations
```java
@Scheduled(cron = "0 0 2 * * *") // 2 AM daily
public void precomputeRecommendations() {
    List<Users> activeUsers = userRepository.findActiveUsers();
    for (Users user : activeUsers) {
        RecommendationResponse rec = recommendForOrder(user.getId());
        cache.put(user.getId(), rec);
    }
}
```

---

## 10. Monitoring & Analytics

### Track Recommendation Acceptance Rate
```java
@PostMapping("/api/recommendations/{userId}/feedback")
public void trackRecommendationFeedback(
        @PathVariable Long userId,
        @RequestParam String recommendedProduct,
        @RequestParam boolean purchased) {
    // Log which recommendations users actually purchase
    // Use this to improve AI prompt over time
}
```

### Monitor API Costs
```java
@Aspect
@Component
public class ApiCostMonitor {
    
    @Around("execution(* AiRecommendationService.*(..))")
    public Object monitorCost(ProceedingJoinPoint joinPoint) throws Throwable {
        // Track OpenAI API calls and costs
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        
        // Log: Duration, tokens used, cost
        log.info("API Call: {}ms", duration);
        
        return result;
    }
}
```

---

## 11. Troubleshooting

### Problem: Getting empty recommendations
**Solution:** 
- Check if user has orders: `SELECT * FROM Orders WHERE user_id = ?`
- Check if database has any orders: `SELECT COUNT(*) FROM Orders`
- Check API key in `application.properties`

### Problem: API calls are slow
**Solution:**
- Implement caching for popular products
- Use database queries instead of loading all orders in Java
- Reduce `max_tokens` parameter

### Problem: Low recommendation quality
**Solution:**
- Add more context to prompt (categories, prices, ratings)
- Use different AI model (gpt-4 vs gpt-4o-mini)
- Add user preferences to prompt
- Adjust `temperature` parameter (0.6 is current default)

---

## 12. Next Steps to Enhance

1. **Add Product Categories** - Group by category in recommendations
2. **Add User Ratings** - Consider highly-rated products
3. **Add Price Sensitivity** - Consider user's spending patterns
4. **Add Inventory** - Don't recommend out-of-stock items
5. **Add Return Rates** - Avoid products with high return rates
6. **Add Seasonal Data** - Season-specific recommendations
7. **Add User Demographics** - Age, location, preferences
8. **Add A/B Testing** - Compare different AI models/prompts


