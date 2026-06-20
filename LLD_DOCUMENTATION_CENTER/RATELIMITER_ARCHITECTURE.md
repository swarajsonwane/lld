# RateLimiter LLD - Architecture & Design

## System Overview

**Project**: API Rate Limiting System  
**Language**: Java 21  
**Patterns**: 3 (Singleton, Factory, Strategy)  
**Components**: 8+ Classes  
**Focus**: Multiple rate limiting algorithms for API protection

---

## 🏗️ Architecture Diagram

### High-Level System

```
                    ┌──────────────────────────┐
                    │ Main (Demo Entry)        │
                    │   Test All Limiters      │
                    └────────────┬─────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
        ┌──────────────────────┐  ┌──────────────────┐
        │LimiterFactory        │  │RateLimiter       │
        │(Factory)             │  │(Interface)       │
        │──────────────────────│  │──────────────────│
        │+ createLimiter()     │  │+ allowRequest()  │
        └──────────┬───────────┘  └────────┬─────────┘
                   │                       │
                   │        ┌──────────────┼──────────────┐
                   │        │              │              │
                   ▼        ▼              ▼              ▼
        ┌──────────────────────┐ ┌────────────────┐ ┌──────────────────┐
        │TokenBucketLimiter    │ │SlidingWindow   │ │FixedWindow       │
        │(Strategy)            │ │LogLimiter      │ │Limiter           │
        │──────────────────────│ │(Strategy)      │ │(Strategy)        │
        │- bucket              │ │────────────────│ │──────────────────│
        │- capacity            │ │- log[]         │ │- window          │
        │- refillRate          │ │- maxRequests   │ │- maxRequests     │
        │+ isAllowed()         │ │+ isAllowed()   │ │+ isAllowed()     │
        └──────────────────────┘ └────────────────┘ └──────────────────┘
```

---

## 🔄 Rate Limiting Algorithms

### 1. Token Bucket Algorithm

```
┌─────────────────────────────────┐
│  Bucket with Tokens             │
│                                 │
│  ● ● ● ● ●  (5 tokens)         │
│  Capacity: 5                    │
│  Refill Rate: 2 tokens/sec      │
└─────────────────────────────────┘

Request Arrives:
├─ Is there a token in bucket?
├─ YES → Consume token, allow request
└─ NO  → Reject request

Refill Process:
├─ Every 1 second
├─ Add 2 tokens (up to capacity 5)
└─ Smooth rate limiting
```

**Flow Diagram**:
```
Request → Check bucket
            ├─ Token available?
            │  ├─ YES → Consume → Allow
            │  └─ NO  → Reject
            └─ Refill timer tick
               └─ Add tokens (up to capacity)
```

### 2. Sliding Window Log Algorithm

```
Timeline:
[Old requests]...[1 min window]...[New requests]
                 ↑
          Current time

Count requests:
├─ Only count within last 60 seconds
├─ If count < limit
│  ├─ Allow request
│  └─ Add to log
└─ If count = limit
   └─ Reject request
```

**Process**:
```
Request → Get current time
          ├─ Remove old entries (> 1 min old)
          ├─ Count remaining requests
          ├─ Is count < limit?
          │  ├─ YES → Allow, add to log
          │  └─ NO  → Reject
          └─ Return result
```

### 3. Fixed Window Counter

```
Window 1        Window 2        Window 3
[60 sec]  |    [60 sec]  |    [60 sec]  |...
 5 reqs   |     2 reqs   |    (new)     |...
 Max: 10  |     Max: 10  |

Current Window Count:
├─ Reset every minute
├─ Count requests in current window
├─ If count < limit
│  ├─ Allow request
│  └─ Increment counter
└─ If count = limit
   └─ Reject request
```

---

## 💾 Data Models

### RateLimitResult
```
RateLimitResult {
    allowed: boolean
    remainingTokens: int (for token bucket)
    resetTime: long (when limit resets)
    clientId: String
    timestamp: long
    message: String
}
```

### TokenBucketLimiter Configuration
```
Configuration {
    capacity: int         (max tokens in bucket)
    refillRate: int       (tokens added per second)
    window: long          (refill interval in ms)
}

Example:
├─ Capacity: 5 tokens
├─ Refill Rate: 2 tokens/second
└─ Allows 5 immediate requests + 2 per second sustained
```

### SlidingWindowLog Configuration
```
Configuration {
    maxRequests: int      (request limit)
    timeWindow: long      (window size in milliseconds)
}

Example:
├─ Max Requests: 100
├─ Time Window: 60000 ms (1 minute)
└─ Allows max 100 requests per minute
```

---

## 🎯 Design Patterns Used

### 1. Factory Pattern
**Where**: LimiterFactory  
**Why**: Create different limiter types  
**Implementation**: Factory method for limiter creation

```java
public RateLimiter createTokenBucketLimiter(int capacity, int refillRate) {
    return new TokenBucketLimiter(capacity, refillRate);
}

public RateLimiter createSlidingWindowLimiter(int maxRequests, long timeWindow) {
    return new SlidingWindowLogLimiter(maxRequests, timeWindow);
}
```

### 2. Strategy Pattern
**Where**: RateLimiter interface & implementations  
**Why**: Different rate limiting algorithms  
**Strategies**:
- TokenBucketLimiter
- SlidingWindowLogLimiter
- FixedWindowLimiter

```java
public interface RateLimiter {
    RateLimitResult allowRequest(String clientId);
}
```

### 3. Singleton Pattern
**Where**: Optional - factory singleton  
**Why**: Single factory instance  
**Usage**: Reuse limiter instances

---

## 🏭 Component Structure

### RateLimiter Interface (Strategy)
```
Responsibilities:
└─ Check if request allowed

Methods:
└─ allowRequest(clientId) → RateLimitResult
```

### TokenBucketLimiter
```
Algorithm: Token Bucket

Data:
├─ bucket: Map<ClientId, Double>
├─ capacity: int
├─ refillRate: int
└─ lastRefill: Map<ClientId, Long>

Process:
├─ Check if tokens available
├─ If yes → Consume, allow
├─ If no  → Reject
└─ Refill at rate
```

### SlidingWindowLogLimiter
```
Algorithm: Sliding Window Log

Data:
├─ logs: Map<ClientId, Queue<Long>>
├─ maxRequests: int
└─ timeWindow: long

Process:
├─ Get request timestamps
├─ Remove old timestamps
├─ Count valid timestamps
├─ Check against limit
└─ Return result
```

### FixedWindowLimiter
```
Algorithm: Fixed Window Counter

Data:
├─ counters: Map<ClientId, Integer>
├─ windows: Map<ClientId, Long>
├─ maxRequests: int
└─ windowDuration: long

Process:
├─ Check window expiry
├─ Reset if expired
├─ Increment counter
├─ Check against limit
└─ Return result
```

---

## 🔬 Algorithm Comparison

| Algorithm | Complexity | Memory | Accuracy | Use Case |
|-----------|-----------|--------|----------|----------|
| Token Bucket | O(1) | O(n) | High | APIs, bandwidth |
| Sliding Window Log | O(n) | O(n*m) | Very High | Precise limiting |
| Fixed Window | O(1) | O(n) | Medium | Simple APIs |

---

## 📊 Scenario Flows

### Scenario 1: Token Bucket
```
Setup:
- Capacity: 5 tokens
- Refill: 2 tokens/sec

Time 0s:
- 5 tokens in bucket
- Requests 1-5: ALLOWED ✓
- Request 6: REJECTED ✗

Time 1s:
- +2 tokens (now 2)
- Request 6: ALLOWED ✓
- Request 7: REJECTED ✗

Time 2s:
- +2 tokens (now 4)
- Requests 7-10: Dependent on bucket state
```

### Scenario 2: Sliding Window Log
```
Setup:
- Max Requests: 5 per 60 seconds

Time 0-30s:
- Requests 1-5: ALLOWED ✓
- Request 6: REJECTED ✗

Time 30-35s:
- No new requests
- Old requests still in window

Time 35-65s:
- Requests 1-2 expire (>60s old)
- New requests can proceed
- Still limited by max 5/window
```

### Scenario 3: Multiple Clients
```
Client A (rate: 10 req/min):
- Sent 8 requests
- Remaining: 2 requests

Client B (rate: 5 req/min):
- Sent 5 requests
- Remaining: 0 requests
- New requests: REJECTED ✗

Each client independent limits
```

---

## 🔗 Component Integration

```
Main Demo
    ├─ LimiterFactory
    │   ├─ createTokenBucket()
    │   ├─ createSlidingWindow()
    │   └─ createFixedWindow()
    │
    ├─ TokenBucketLimiter (RateLimiter)
    │   └─ allowRequest() → RateLimitResult
    │
    ├─ SlidingWindowLogLimiter (RateLimiter)
    │   └─ allowRequest() → RateLimitResult
    │
    └─ RateLimitResult
        ├─ allowed: boolean
        ├─ remainingTokens: int
        ├─ resetTime: long
        └─ message: String
```

---

## 💡 Key Features

✓ **Multiple Algorithms**
- Token Bucket: Smooth rate limiting
- Sliding Window: Precise limiting
- Fixed Window: Simple limiting

✓ **Per-Client Tracking**
- Individual limits per client
- Isolated token buckets
- Independent windows

✓ **Performance Monitoring**
- Track remaining capacity
- Monitor reset times
- Request statistics

✓ **Easy Integration**
- Simple allowRequest() method
- Returns detailed result
- Configurable per limiter

---

## 🚀 Execution Flow

```
START
  │
  ├─► Demo 1: Token Bucket
  │   ├─ Create limiter (cap: 5, rate: 2)
  │   ├─ Send requests 1-5
  │   ├─ Requests 1-5: ALLOWED
  │   ├─ Request 6: REJECTED
  │   ├─ Wait and refill
  │   └─ Display results
  │
  ├─► Demo 2: Sliding Window
  │   ├─ Create limiter (max: 100, window: 60s)
  │   ├─ Send 100 requests
  │   ├─ All allowed
  │   ├─ Request 101: REJECTED
  │   ├─ Wait for window expiry
  │   └─ Display results
  │
  ├─► Demo 3: Multiple Endpoints
  │   ├─ Create 3 limiters
  │   ├─ Send requests to each
  │   ├─ Track per-endpoint limits
  │   └─ Display aggregate stats
  │
  └─► END (All demos complete)
```

---

## 📈 Complexity Analysis

| Operation | Token Bucket | Sliding Window | Fixed Window |
|-----------|-------------|----------------|--------------|
| Check Limit | O(1) | O(n) | O(1) |
| Update | O(1) | O(1) | O(1) |
| Memory | O(1) per client | O(n) per client | O(1) per client |

---

## 🎓 Learning Outcomes

From this project, you'll understand:

1. **Factory Pattern**: Creating different limiter types
2. **Strategy Pattern**: Interchangeable algorithms
3. **Rate Limiting**: Real-world API protection
4. **Algorithm Tradeoffs**: Speed vs accuracy
5. **Concurrent Systems**: Per-client tracking
6. **Performance Optimization**: Efficient data structures

---

## 🔍 Extension Points

1. **Add leaky bucket** → New limiter implementation
2. **Add distributed rate limiting** → Cache backend
3. **Add analytics** → Track request patterns
4. **Add adaptive limiting** → Dynamic configuration
5. **Add circuit breaker** → Fail-safe mechanism
6. **Add client prioritization** → VIP clients

---

*RateLimiter LLD Architecture Document - Complete Reference*
*Last Updated: June 17, 2026*

