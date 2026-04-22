package model;

import java.util.HashMap;
import java.util.Map;

import java.util.*;
import java.util.concurrent.*;

public class TokenBucketLimiter implements Limiter {
    private final int capacity;
    private final int refillRatePerSecond;
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketLimiter(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public RateLimitResult allow(String key) {
        // Atomically get or create bucket
        TokenBucket bucket = buckets.computeIfAbsent(key,
                k -> new TokenBucket(capacity, System.currentTimeMillis())
        );

        // Calculate result values while holding lock on bucket
        boolean allowed;
        int remaining;
        Long retryAfterMs;

        synchronized(bucket) {
            long now = System.currentTimeMillis();
            long elapsed = now - bucket.lastRefillTime;
            double tokensToAdd = (elapsed * refillRatePerSecond) / 1000.0;
            bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
            bucket.lastRefillTime = now;

            if (bucket.tokens >= 1) {
                bucket.tokens -= 1;
                allowed = true;
                remaining = (int) Math.floor(bucket.tokens);
                retryAfterMs = null;
            } else {
                double tokensNeeded = 1 - bucket.tokens;
                allowed = false;
                remaining = 0;
                retryAfterMs = (long) Math.ceil((tokensNeeded * 1000) / refillRatePerSecond);
            }
        }

        // Construct result outside the lock
        return new RateLimitResult(allowed, remaining, retryAfterMs);
    }

    static class TokenBucket {
        double tokens;
        long lastRefillTime;

        TokenBucket(double initialTokens, long time) {
            this.tokens = initialTokens;
            this.lastRefillTime = time;
        }
    }
}

