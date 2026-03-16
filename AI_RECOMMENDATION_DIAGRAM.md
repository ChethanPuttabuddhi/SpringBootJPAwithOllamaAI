# AI Recommendation Service - Data Flow Diagram

## High-Level Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           REST API REQUEST                               │
│  POST /api/recommendations?userId=5                                      │
│  Body: { userId: 5 }                                                     │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│              RecommendationController.getRecommendations()               │
│  - Receives userId parameter                                             │
│  - Calls aiRecommendationService.recommendForOrder(userId)              │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│            AiRecommendationService.recommendForOrder(5)                  │
│                                                                          │
│  1. Fetch user's order history:                                         │
│     ┌─────────────────────────────────────────────────┐                │
│     │ OrderRepository.findByUserId(5)                 │                │
│     │ → Returns: [iPhone($999), MacBook($1299), ...]  │                │
│     └─────────────────────────────────────────────────┘                │
│                                                                          │
│  2. Build comprehensive AI prompt by gathering:                         │
│     ┌──────────────────────────────────────────────────────────┐        │
│     │ A) User's Own History                                    │        │
│     │    getPopularProductsInfo()                              │        │
│     │    → Most popular products: iPhone (20x), MacBook (15x)  │        │
│     │                                                          │        │
│     │ B) Co-Purchase Patterns                                  │        │
│     │    getCoPurchasePatterns(userOrders)                     │        │
│     │    → Products bought by similar users:                   │        │
│     │      AppleCare (18x with iPhone)                         │        │
│     │      AirPods (14x with MacBook)                          │        │
│     │                                                          │        │
│     │ C) Market Trends                                         │        │
│     │    getMarketTrends()                                     │        │
│     │    → Avg order: $1100                                    │        │
│     │    → Trending: USB-C, Apple Watch, iPad                  │        │
│     └──────────────────────────────────────────────────────────┘        │
│                                                                          │
│  3. Construct AI Prompt:                                                │
│     ┌──────────────────────────────────────────────────────────┐        │
│     │ "You are a product recommender AI...                     │        │
│     │                                                          │        │
│     │ CURRENT USER PROFILE:                                    │        │
│     │ User ID: 5                                              │        │
│     │ Order History: iPhone ($999), MacBook ($1299)            │        │
│     │                                                          │        │
│     │ MARKET INTELLIGENCE:                                     │        │
│     │ Popular: iPhone (20x), MacBook (15x), AirPods (12x)      │        │
│     │ Co-Purchase: AppleCare (18x), AirPods (14x), Cable (10x) │        │
│     │ Trends: Avg $1100, USB-C trending, Apple Watch rising    │        │
│     │                                                          │        │
│     │ Based on all this, recommend 2-3 products..."            │        │
│     └──────────────────────────────────────────────────────────┘        │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                      OPENAI API CALL                                     │
│                                                                          │
│  POST https://api.openai.com/v1/chat/completions                        │
│  {                                                                       │
│    "model": "gpt-4o-mini",                                              │
│    "messages": [{                                                       │
│      "role": "user",                                                    │
│      "content": "[comprehensive prompt with all market data]"           │
│    }],                                                                  │
│    "max_tokens": 200,                                                  │
│    "temperature": 0.6                                                  │
│  }                                                                       │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                    OPENAI RESPONSE                                       │
│                                                                          │
│  {                                                                       │
│    "choices": [{                                                        │
│      "message": {                                                       │
│        "content": "AirPods (User bought iPhone before, trending),        │
│                   AppleCare (Co-purchased with MacBook users),           │
│                   USB-C Hub (Trending with tech buyers)"                │
│      }                                                                  │
│    }]                                                                   │
│  }                                                                       │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│         Parse & Format Response                                          │
│                                                                          │
│  extractText(response)   → Get AI's text                                 │
│  parseRecommendations()  → Split into list                              │
│                                                                          │
│  Result: [                                                              │
│    "AirPods (User bought iPhone before, trending)",                     │
│    "AppleCare (Co-purchased with MacBook users)",                       │
│    "USB-C Hub (Trending with tech buyers)"                              │
│  ]                                                                       │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│              Return RecommendationResponse Object                        │
│                                                                          │
│  {                                                                       │
│    "recommendations": [                                                 │
│      "AirPods (User bought iPhone before, trending)",                  │
│      "AppleCare (Co-purchased with MacBook users)",                    │
│      "USB-C Hub (Trending with tech buyers)"                           │
│    ],                                                                   │
│    "raw": "Full AI response text..."                                   │
│  }                                                                       │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│              HTTP 200 Response to Client                                 │
│                                                                          │
│  {                                                                       │
│    "recommendations": [                                                 │
│      "AirPods (User bought iPhone before, trending)",                  │
│      "AppleCare (Co-purchased with MacBook users)",                    │
│      "USB-C Hub (Trending with tech buyers)"                           │
│    ],                                                                   │
│    "raw": "Full AI response text..."                                   │
│  }                                                                       │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## Database Queries Made

```
┌─────────────────────────────────────────────────────────────┐
│          All Database Queries Executed                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ 1. SELECT o FROM Orders o WHERE o.user.id = 5              │
│    Result: [iPhone($999), MacBook($1299)]                  │
│                                                             │
│ 2. SELECT * FROM Orders                                    │
│    Purpose: Get all orders for popularity analysis         │
│    Result: 100 orders from all users                       │
│                                                             │
│ 3. SELECT * FROM Orders (same as #2)                       │
│    Purpose: Analyze co-purchase patterns                   │
│    Result: 100 orders from all users                       │
│                                                             │
│ 4. SELECT * FROM Orders (same as #2)                       │
│    Purpose: Analyze market trends                          │
│    Result: 100 orders from all users                       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Note:** The queries could be optimized with caching or custom database queries to avoid 3 SELECT * calls.

---

## Data Aggregation Process

### Popular Products Algorithm
```
Input: All 100 orders from database
Process:
  1. Filter out null product names
  2. Group by product name
  3. Count occurrences
  4. Sort by count descending
  5. Take top 5

Example:
  iPhone → 20
  MacBook → 15
  AirPods → 12
  USB-C → 8
  AppleCare → 7

Output: "iPhone (20x), MacBook (15x), AirPods (12x), USB-C (8x), AppleCare (7x)"
```

### Co-Purchase Patterns Algorithm
```
Input: 
  - User's past orders: [iPhone, MacBook]
  - All 100 orders from database

Process:
  1. Extract user's products: {iPhone, MacBook}
  2. For each order in all orders:
     If product NOT in user's products:
       Count it
  3. Sort by count descending
  4. Take top 3

Example:
  AppleCare → 18 (other users who bought iPhone/MacBook also bought this)
  AirPods → 14
  USB-C → 10
  Adapters → 8

Output: "AppleCare (18x), AirPods (14x), USB-C (10x)"
```

### Market Trends Algorithm
```
Input: All 100 orders from database

Process:
  1. Calculate average order amount:
     Sum all order amounts / number of orders = $1,100 avg
  
  2. Get last 10 orders by creation date
  3. Extract product names
  4. Remove duplicates

Output: 
  "Avg order value: $1,100 | Trending: iPhone, MacBook, AirPods, USB-C, AppleCare"
```

---

## Why This Is Powerful

```
BEFORE (Simple Recommendation):
  "Hey user 5, you bought iPhone and MacBook. 
   Here are 5 random tech products."
  → Generic, not smart

AFTER (Enhanced with Market Intelligence):
  "Based on YOUR history (iPhone, MacBook),
   WHAT OTHERS BUY (AppleCare, AirPods),
   WHAT SELLS TOGETHER (USB-C with iPhones),
   and CURRENT TRENDS (Apple Watch rising),
   I recommend: AirPods, AppleCare, USB-C Hub"
  → Personalized, social, complementary, trendy
```

---

## Configuration Needed

**`application.properties`:**
```properties
# OpenAI API Configuration
spring.ai.openai.api-key=sk-proj-xxxxxxxxxxxxx
spring.ai.openai.chat.options.model=gpt-4o-mini
```

**Get your API key from:** https://platform.openai.com/api-keys

---

## Performance Considerations

**Current:** 3 full table scans per recommendation request
**Recommendation:** Optimize with:

1. **Caching:**
   ```java
   @Cacheable("popularProducts")
   private String getPopularProductsInfo() { ... }
   ```

2. **Scheduled Updates:**
   ```java
   @Scheduled(fixedRate = 300000) // Update every 5 minutes
   public void refreshMarketData() { ... }
   ```

3. **Custom Database Queries:**
   ```java
   @Query("SELECT p.name, COUNT(*) FROM Orders p 
           GROUP BY p.name ORDER BY COUNT(*) DESC LIMIT 5")
   List<Object[]> getTopProducts();
   ```

---

## Summary

✅ **User-Specific:** Uses individual purchase history
✅ **Community-Aware:** Leverages all users' data
✅ **Complementary:** Finds products bought together
✅ **Trend-Aware:** Includes market direction
✅ **AI-Powered:** OpenAI makes the final decision
✅ **Extensible:** Easy to add more signals (ratings, returns, price, etc.)

