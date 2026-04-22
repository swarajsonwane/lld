import model.RateLimitResult;
import model.TokenBucketLimiter;
import model.SlidingWindowLogLimiter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Rate Limiter Demo ===\n");
        
        // Demo 1: Token Bucket Limiter
        demoTokenBucket();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 2: Sliding Window Log Limiter
        demoSlidingWindowLog();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 3: RateLimiter with multiple endpoints
        demoRateLimiterWithEndpoints();
    }
    
    private static void demoTokenBucket() {
        System.out.println("DEMO 1: Token Bucket Limiter");
        System.out.println("Configuration: 5 tokens capacity, 2 tokens refill per second\n");
        
        TokenBucketLimiter limiter = new TokenBucketLimiter(5, 2);
        String clientId = "client-1";
        
        // Test 5 consecutive requests (should all be allowed initially)
        System.out.println("Sending 5 consecutive requests:");
        for (int i = 1; i <= 5; i++) {
            RateLimitResult result = limiter.allow(clientId);
            System.out.printf("  Request %d: %s (Remaining: %d)%n", 
                i, result.isAllowed() ? "ALLOWED" : "BLOCKED", result.getRemaining());
        }
        
        // 6th request should be blocked
        System.out.println("\nSending 6th request (should be blocked):");
        RateLimitResult result = limiter.allow(clientId);
        System.out.printf("  Request 6: %s", result.isAllowed() ? "ALLOWED" : "BLOCKED");
        if (result.getRetryAfterMs() != null) {
            System.out.printf(" (Retry after: %d ms)%n", result.getRetryAfterMs());
        } else {
            System.out.println();
        }
        
        // Wait and test again
        System.out.println("\nWaiting 2 seconds for token refill...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Sending requests again after refill:");
        for (int i = 1; i <= 3; i++) {
            result = limiter.allow(clientId);
            System.out.printf("  Request %d: %s (Remaining: %d)%n", 
                i, result.isAllowed() ? "ALLOWED" : "BLOCKED", result.getRemaining());
        }
    }
    
    private static void demoSlidingWindowLog() {
        System.out.println("DEMO 2: Sliding Window Log Limiter");
        System.out.println("Configuration: 3 requests per 5 seconds\n");
        
        SlidingWindowLogLimiter limiter = new SlidingWindowLogLimiter(3, 5000);
        String clientId = "client-2";
        
        System.out.println("Sending 3 requests (should all be allowed):");
        for (int i = 1; i <= 3; i++) {
            RateLimitResult result = limiter.allow(clientId);
            System.out.printf("  Request %d: %s (Remaining: %d)%n", 
                i, result.isAllowed() ? "ALLOWED" : "BLOCKED", result.getRemaining());
        }
        
        System.out.println("\nSending 4th request (should be blocked):");
        RateLimitResult result = limiter.allow(clientId);
        System.out.printf("  Request 4: %s", result.isAllowed() ? "ALLOWED" : "BLOCKED");
        if (result.getRetryAfterMs() != null) {
            System.out.printf(" (Retry after: %d ms)%n", result.getRetryAfterMs());
        } else {
            System.out.println();
        }
        
        System.out.println("\nWaiting 5 seconds for window to slide...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Sending request again (should be allowed after window expires):");
        result = limiter.allow(clientId);
        System.out.printf("  Request: %s (Remaining: %d)%n", 
            result.isAllowed() ? "ALLOWED" : "BLOCKED", result.getRemaining());
    }
    
    private static void demoRateLimiterWithEndpoints() {
        System.out.println("DEMO 3: RateLimiter with Multiple Endpoints");
        System.out.println("Different rate limits for different endpoints\n");
        
        // Create config for multiple endpoints
        List<Map<String, Object>> configs = new ArrayList<>();
        
        // Config for /api/auth endpoint - stricter
        Map<String, Object> authConfig = new HashMap<>();
        authConfig.put("endpoint", "/api/auth");
        authConfig.put("type", "TOKEN_BUCKET");
        authConfig.put("capacity", 3);
        authConfig.put("refillRate", 1);
        configs.add(authConfig);
        
        // Config for /api/data endpoint - more lenient
        Map<String, Object> dataConfig = new HashMap<>();
        dataConfig.put("endpoint", "/api/data");
        dataConfig.put("type", "TOKEN_BUCKET");
        dataConfig.put("capacity", 10);
        dataConfig.put("refillRate", 5);
        configs.add(dataConfig);
        
        // Default config for other endpoints
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("type", "TOKEN_BUCKET");
        defaultConfig.put("capacity", 5);
        defaultConfig.put("refillRate", 2);
        
        RateLimiter rateLimiter = new RateLimiter(configs, defaultConfig);
        String clientId = "user-123";
        
        // Test /api/auth endpoint
        System.out.println("Testing /api/auth endpoint (capacity: 3, refill: 1/sec):");
        for (int i = 1; i <= 4; i++) {
            RateLimitResult result = rateLimiter.allow(clientId, "/api/auth");
            System.out.printf("  Request %d: %s%n", i, result.isAllowed() ? "ALLOWED" : "BLOCKED");
        }
        
        // Test /api/data endpoint
        System.out.println("\nTesting /api/data endpoint (capacity: 10, refill: 5/sec):");
        for (int i = 1; i <= 5; i++) {
            RateLimitResult result = rateLimiter.allow(clientId, "/api/data");
            System.out.printf("  Request %d: %s (Remaining: %d)%n", 
                i, result.isAllowed() ? "ALLOWED" : "BLOCKED", result.getRemaining());
        }
        
        // Test unknown endpoint (uses default)
        System.out.println("\nTesting /api/unknown endpoint (uses default: capacity: 5, refill: 2/sec):");
        for (int i = 1; i <= 3; i++) {
            RateLimitResult result = rateLimiter.allow(clientId, "/api/unknown");
            System.out.printf("  Request %d: %s (Remaining: %d)%n", 
                i, result.isAllowed() ? "ALLOWED" : "BLOCKED", result.getRemaining());
        }
    }
}

