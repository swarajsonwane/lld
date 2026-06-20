# RateLimiter LLD - Project Structure

## 📁 Directory Layout

```
rateLimiter/
├── rateLimiter.iml                      # IntelliJ configuration (Java 21)
├── build.sh                             # Build script
├── run.sh                               # Run script
└── src/
    ├── Main.java                        # Demo entry point
    ├── RateLimiter.java                 # Main rate limiter interface
    │
    ├── factory/
    │   └── LimiterFactory.java          # Factory for creating limiters
    │
    └── model/
        ├── RateLimitResult.java         # Result object
        ├── TokenBucketLimiter.java      # Token bucket implementation
        ├── SlidingWindowLogLimiter.java # Sliding window implementation
        └── FixedWindowLimiter.java      # Fixed window implementation
```

---

## 📄 File Descriptions

### Main Class

#### `Main.java`
**Purpose**: Entry point and demonstration  
**Responsibilities**:
- Initialize limiters
- Run test scenarios
- Display results

**Demo Sections**:
```
1. Token Bucket Demo
   └─ Show token consumption and refill

2. Sliding Window Log Demo
   └─ Show window-based limiting

3. Multi-endpoint Demo
   └─ Show multiple limiters working together
```

---

### Interfaces

#### `RateLimiter.java`
```java
public interface RateLimiter {
    RateLimitResult allowRequest(String clientId);
}
```

**Purpose**: Strategy interface for rate limiting

**Methods**:
- `allowRequest(clientId)` → RateLimitResult

---

### Factory (`factory/`)

#### `LimiterFactory.java`
```
Factory for creating rate limiters

Responsibilities:
├─ Create token bucket limiter
├─ Create sliding window limiter
├─ Create fixed window limiter
└─ Manage limiter instances

Methods:
├─ createTokenBucketLimiter(capacity, refillRate)
├─ createSlidingWindowLimiter(maxRequests, timeWindow)
├─ createFixedWindowLimiter(maxRequests, windowDuration)
└─ getOrCreateLimiter(type, config)
```

---

### Models (`model/`)

#### `RateLimitResult.java`
```
Result object returned from allowRequest()

Fields:
├─ allowed: boolean        (was request allowed?)
├─ remainingTokens: int    (for token bucket)
├─ requestsRemaining: int  (for window-based)
├─ resetTime: long         (milliseconds until reset)
├─ clientId: String        (which client)
├─ timestamp: long         (when checked)
└─ message: String         (description)

Methods:
├─ isAllowed()
├─ getRemainingTokens()
├─ getResetTime()
└─ toString()
```

---

#### `TokenBucketLimiter.java`
**Algorithm**: Token Bucket

```
Responsibilities:
├─ Maintain token buckets per client
├─ Refill tokens at constant rate
├─ Check token availability
└─ Consume tokens on request

Data Structures:
├─ buckets: Map<ClientId, Double>
│  └─ Current tokens in bucket
├─ capacity: int
│  └─ Maximum tokens in bucket
├─ refillRate: int
│  └─ Tokens added per second
└─ lastRefillTime: Map<ClientId, Long>
   └─ Last refill timestamp per client

Key Methods:
├─ constructor(capacity, refillRate)
├─ allowRequest(clientId)
│  ├─ Refill tokens if needed
│  ├─ Check if tokens available
│  ├─ Consume token if allowed
│  └─ Return RateLimitResult
└─ refillBucket(clientId)
   ├─ Calculate elapsed time
   ├─ Add tokens: elapsed * refillRate / 1000
   ├─ Cap at capacity
   └─ Update lastRefillTime

Algorithm:
1. Get current client bucket
2. Calculate tokens to add: (currentTime - lastRefill) * refillRate
3. Add tokens, cap at capacity
4. If bucket > 0:
   - Consume 1 token
   - Return ALLOWED
5. Else:
   - Return REJECTED
   - Calculate wait time
```

**Example Configuration**:
```
Capacity: 5 tokens
Refill Rate: 2 tokens/second

Usage Pattern:
- Time 0s: 5 tokens → Requests 1-5 ALLOWED
- Time 0s: 0 tokens → Request 6 REJECTED
- Time 0.5s: 1 token → Request 6 ALLOWED
- Time 1.0s: 2 tokens → Requests 7-8 ALLOWED (if attempted)
```

---

#### `SlidingWindowLogLimiter.java`
**Algorithm**: Sliding Window Log

```
Responsibilities:
├─ Maintain request logs per client
├─ Count requests in current window
├─ Remove expired requests
└─ Check against limit

Data Structures:
├─ requestLogs: Map<ClientId, Queue<Long>>
│  └─ Timestamps of past requests
├─ maxRequests: int
│  └─ Request limit per window
└─ timeWindow: long
   └─ Window duration in milliseconds

Key Methods:
├─ constructor(maxRequests, timeWindow)
├─ allowRequest(clientId)
│  ├─ Get client's request log
│  ├─ Remove old requests (> timeWindow)
│  ├─ Count remaining requests
│  ├─ If count < max:
│  │  ├─ Add current timestamp
│  │  └─ Return ALLOWED
│  └─ Else:
│     └─ Return REJECTED
└─ removeExpiredRequests(clientId)
   ├─ Get current time
   ├─ Calculate window start: now - timeWindow
   ├─ Remove all timestamps < window start
   └─ Keep only valid requests

Algorithm:
1. Get current client log
2. Get current timestamp: now
3. Window start: now - timeWindow
4. Remove all entries < window start
5. Count remaining entries
6. If count < maxRequests:
   - Add now to log
   - Return ALLOWED with remaining count
7. Else:
   - Return REJECTED with next reset time
```

**Example Configuration**:
```
Max Requests: 100 per 60000ms (1 minute)

Timeline Example:
0s: Requests 1-100 → All ALLOWED
0s: Request 101 → REJECTED
30s: Requests 1-100 still in window
60s: Requests 1-100 expire
60s: New requests allowed again
```

---

#### `FixedWindowLimiter.java`
**Algorithm**: Fixed Window Counter

```
Responsibilities:
├─ Maintain counter per client
├─ Reset counter per window
├─ Check against limit
└─ Track window boundaries

Data Structures:
├─ counters: Map<ClientId, Integer>
│  └─ Request count in current window
├─ windows: Map<ClientId, Long>
│  └─ Current window start time
├─ maxRequests: int
│  └─ Request limit per window
└─ windowDuration: long
   └─ Duration in milliseconds

Key Methods:
├─ constructor(maxRequests, windowDuration)
├─ allowRequest(clientId)
│  ├─ Get client's window info
│  ├─ Check if window expired
│  ├─ If expired:
│  │  ├─ Reset counter to 0
│  │  └─ Update window start
│  ├─ If counter < max:
│  │  ├─ Increment counter
│  │  └─ Return ALLOWED
│  └─ Else:
│     └─ Return REJECTED
└─ calculateResetTime(clientId)
   └─ window start + windowDuration

Algorithm:
1. Get client's counter and window
2. Current time: now
3. If now >= window + duration:
   - Window expired, reset
   - Set window = now
   - Set counter = 0
4. If counter < maxRequests:
   - counter++
   - Return ALLOWED
5. Else:
   - Return REJECTED
   - Calculate reset time: window + duration
```

**Example Configuration**:
```
Max Requests: 10 per 60000ms (1 minute)

Timeline:
0s-59s: Window 1
        Counter: 0-10 (ALLOWED until 10)
        Request 11: REJECTED

60s-119s: Window 2
          Counter resets to 0
          New window starts
          Requests 1-10: ALLOWED
```

---

## 📊 Class Relationships

```
Main
    │
    ├─ LimiterFactory
    │   ├─ createTokenBucket()
    │   ├─ createSlidingWindow()
    │   └─ createFixedWindow()
    │
    └─ RateLimiter (Interface)
        ├─ TokenBucketLimiter
        │   └─ allowRequest(clientId)
        │
        ├─ SlidingWindowLogLimiter
        │   └─ allowRequest(clientId)
        │
        ├─ FixedWindowLimiter
        │   └─ allowRequest(clientId)
        │
        └─ RateLimitResult
            ├─ allowed: boolean
            ├─ remainingTokens: int
            ├─ resetTime: long
            └─ message: String
```

---

## 🔄 Request Flow

### Token Bucket Request
```
allowRequest("client-1")
    │
    ├─► Get bucket for client-1
    ├─► Calculate time elapsed
    ├─► Add refill tokens
    ├─► Cap at capacity
    ├─► Is bucket > 0?
    │   ├─ YES → Consume 1 token → Return ALLOWED
    │   └─ NO → Return REJECTED with wait time
    │
    └─► Return RateLimitResult
```

### Sliding Window Request
```
allowRequest("client-2")
    │
    ├─► Get request log for client-2
    ├─► Get current time
    ├─► Calculate window start: now - timeWindow
    ├─► Remove old entries < window start
    ├─► Count valid entries
    ├─► Is count < maxRequests?
    │   ├─ YES → Add now to log → Return ALLOWED
    │   └─ NO → Return REJECTED
    │
    └─► Return RateLimitResult
```

### Fixed Window Request
```
allowRequest("client-3")
    │
    ├─► Get counter and window for client-3
    ├─► Is window expired?
    │   ├─ YES → Reset counter and window
    │   └─ NO → Continue
    ├─► Is counter < maxRequests?
    │   ├─ YES → Increment counter → Return ALLOWED
    │   └─ NO → Return REJECTED
    │
    └─► Return RateLimitResult
```

---

## 💾 Data Structures

### TokenBucketLimiter
```
buckets: {
    "client-1": 4.5,
    "client-2": 3.2,
    "client-3": 0.0
}

lastRefillTime: {
    "client-1": 1624512345000,
    "client-2": 1624512344500,
    "client-3": 1624512344000
}
```

### SlidingWindowLogLimiter
```
requestLogs: {
    "client-1": [1624512345000, 1624512345100, ...],
    "client-2": [1624512345200, 1624512345300, ...],
    "client-3": []
}
```

### FixedWindowLimiter
```
counters: {
    "client-1": 8,
    "client-2": 10,
    "client-3": 3
}

windows: {
    "client-1": 1624512300000,  // Window start time
    "client-2": 1624512300000,
    "client-3": 1624512300000
}
```

---

## 🎯 Test Scenarios

### Scenario 1: Token Bucket
```
Configuration: Capacity=5, Refill Rate=2 tokens/sec

Test Case:
1. Request 1-5: ALLOWED (consume 5 tokens)
2. Request 6: REJECTED (0 tokens)
3. Wait 0.5s: Refill 1 token
4. Request 6: ALLOWED (consume 1 token)
5. Wait 1s: Refill 2 tokens
6. Request 7-8: ALLOWED
7. Request 9: REJECTED
```

### Scenario 2: Sliding Window
```
Configuration: Max Requests=5, Time Window=60000ms

Test Case:
1. Requests 1-5: ALLOWED (within window)
2. Request 6: REJECTED (limit reached)
3. Wait 60s: Window expires
4. Request 6: ALLOWED (new window)
```

### Scenario 3: Fixed Window
```
Configuration: Max Requests=10, Window Duration=60000ms

Test Case:
1. Requests 1-10: ALLOWED (window 1)
2. Request 11: REJECTED (limit)
3. Wait 60s: New window
4. Request 11 (now 1 in window 2): ALLOWED
```

---

## 🚀 Build & Run

```bash
# Build
javac -d out/production/rateLimiter $(find src -name "*.java" -type f)

# Run
java -cp out/production/rateLimiter Main

# Using scripts
bash build.sh
bash run.sh
```

---

## 📋 Configuration Examples

### Token Bucket
```java
RateLimiter limiter = new TokenBucketLimiter(5, 2);
// Capacity: 5 tokens
// Refill: 2 tokens/second
```

### Sliding Window
```java
RateLimiter limiter = new SlidingWindowLogLimiter(100, 60000);
// Max Requests: 100
// Time Window: 60000ms (1 minute)
```

### Fixed Window
```java
RateLimiter limiter = new FixedWindowLimiter(10, 60000);
// Max Requests: 10
// Window Duration: 60000ms (1 minute)
```

---

## 📈 Statistics

| Metric | Count |
|--------|-------|
| Total Files | 7 |
| Classes | 6 |
| Interfaces | 1 |
| Algorithms | 3 |
| Factory Methods | 3 |

---

## 🎓 Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| Factory | LimiterFactory | Create limiters |
| Strategy | RateLimiter interface | Different algorithms |
| Singleton | Optional LimiterFactory | Reuse instances |

---

## 🔗 Extension Points

1. **Add leaky bucket** → New implementation
2. **Add sliding window counter** → Hybrid approach
3. **Add distributed limiting** → Redis backend
4. **Add metrics collection** → Track stats
5. **Add adaptive limits** → Dynamic configuration
6. **Add cost-based limiting** → Weight requests

---

## 💡 Performance Characteristics

### Space Complexity
```
Token Bucket: O(1) per client
Sliding Window: O(n) per client (n = requests in window)
Fixed Window: O(1) per client
```

### Time Complexity
```
Token Bucket: O(1) per request
Sliding Window: O(n) worst case (cleanup old entries)
Fixed Window: O(1) per request
```

---

*RateLimiter Project Structure - Complete Guide*
*Last Updated: June 17, 2026*

