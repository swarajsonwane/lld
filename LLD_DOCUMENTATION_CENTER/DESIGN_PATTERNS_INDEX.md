# Design Patterns Index - Complete Reference

## 📚 Design Patterns Across All Projects

This document provides a comprehensive index of all design patterns used across all LLD projects, with cross-project references and learning paths.

---

## 🎯 Patterns Overview

### Total Patterns Implemented: 8 Unique Patterns

| # | Pattern | Category | Frequency | Complexity |
|---|---------|----------|-----------|-----------|
| 1 | Singleton | Creational | 5/5 projects | ⭐⭐ |
| 2 | Factory | Creational | 4/5 projects | ⭐⭐ |
| 3 | Builder | Creational | 2/5 projects | ⭐⭐⭐ |
| 4 | State | Behavioral | 3/5 projects | ⭐⭐⭐⭐ |
| 5 | Strategy | Behavioral | 5/5 projects | ⭐⭐⭐ |
| 6 | Observer | Behavioral | 2/5 projects | ⭐⭐⭐ |
| 7 | Command | Behavioral | 1/5 projects | ⭐⭐⭐ |
| 8 | Composite | Structural | 1/5 projects | ⭐⭐⭐ |
| 9 | Facade | Structural | 1/5 projects | ⭐⭐⭐ |
| 10 | Decorator | Structural | 1/5 projects | ⭐⭐⭐ |

---

## 🏗️ Creational Patterns

### 1. Singleton Pattern

**Purpose**: Ensure single instance of a class  
**Frequency**: 5/5 projects (Most common)

#### Implementations

| Project | Class | Purpose | File |
|---------|-------|---------|------|
| Netflix | NetflixSystem | Single streaming system | `src/NetflixSystem.java` |
| CoffeeMachine | Inventory | Single inventory manager | `singleton/Inventory.java` |
| PaymentSystem | PaymentGatewayService | Single gateway | `src/PaymentGatewayService.java` |
| Spotify | MusicStreamingSystem | Single music system | `src/MusicStreamingSystem.java` |
| RateLimiter | (Optional) | Factory singleton | `factory/LimiterFactory.java` |

#### Key Characteristics

```java
// Thread-safe double-checked locking
private static volatile ClassName instance;

public static ClassName getInstance() {
    if (instance == null) {
        synchronized (ClassName.class) {
            if (instance == null) {
                instance = new ClassName();
            }
        }
    }
    return instance;
}
```

#### Use Cases
- ✓ Single system manager needed
- ✓ Shared resources (inventory, gateway)
- ✓ Coordinated operations
- ✓ Database connections

#### Related Patterns
- Factory → Often used with Singleton for instance creation
- Builder → Can create singleton instances

---

### 2. Factory Pattern

**Purpose**: Create objects without specifying exact classes  
**Frequency**: 4/5 projects

#### Implementations

| Project | Class | Creates | File |
|---------|-------|---------|------|
| Netflix | ContentFactory | Movie/Series/Episode | `factory/ContentFactory.java` |
| PaymentSystem | PaymentProcessorFactory | Payment processors | `factory/PaymentProcessorFactory.java` |
| RateLimiter | LimiterFactory | Rate limiters | `factory/LimiterFactory.java` |
| CoffeeMachine | (Implicit) | Coffee objects | Via decorators |

#### Factory Patterns Used

**Simple Factory**:
```java
public PaymentProcessor createProcessor(PaymentMethod method) {
    return switch(method) {
        case CREDIT_CARD -> new CreditCardProcessor();
        case PAYPAL -> new PayPalProcessor();
        case UPI -> new UPIProcessor();
    };
}
```

**Factory Method** (Content Factory):
```java
public Movie createMovie(String title, Genre genre, int duration, int year) {
    movieCounter++;
    String id = "M" + movieCounter;
    return new Movie(id, title, genre, duration, year);
}
```

#### Key Features
- ✓ Hides object creation complexity
- ✓ Auto-generates IDs
- ✓ Encapsulates instantiation logic
- ✓ Enables easy object replacement

---

### 3. Builder Pattern

**Purpose**: Construct complex objects step by step  
**Frequency**: 2/5 projects

#### Implementations

| Project | Class | Builds | File |
|---------|-------|--------|------|
| Netflix | User.Builder | User objects | `models/User.java` |
| Spotify | User.Builder | User objects | `models/User.java` |
| PaymentSystem | PaymentRequest.Builder | Payment requests | `models/PaymentRequest.java` |

#### Builder Pattern Structure

```java
// Netflix/Spotify Example
User user = new User.Builder()
    .setId("U001")
    .setName("John")
    .setEmail("john@email.com")
    .setSubscriptionPlan(SubscriptionPlan.PREMIUM)
    .setStreamingStrategy(new PremiumStreamingStrategy())
    .build();

// PaymentSystem Example
PaymentRequest request = new PaymentRequest.Builder()
    .payerId("U-123")
    .amount(150.75)
    .currency("INR")
    .paymentMethod(PaymentMethod.CREDIT_CARD)
    .paymentDetails(cardDetailsMap)
    .build();
```

#### Benefits
- ✓ Fluent API interface
- ✓ Optional parameters
- ✓ Immutable objects
- ✓ Better readability

---

## 🎭 Behavioral Patterns

### 4. State Pattern

**Purpose**: Change object behavior based on state  
**Frequency**: 3/5 projects

#### Implementations

| Project | Context | States | File |
|---------|---------|--------|------|
| Netflix | VideoPlayer | PlayingState, PausedState, StoppedState | `player/*` |
| CoffeeMachine | VendingMachine | SelectingState, PaidState, ReadyState, OutOfIngredientState | `state/*` |
| Spotify | Player | PlayingState, PausedState, StoppedState | `states/*` |

#### State Transitions

```
Netflix/Spotify Video Player:
STOPPED → PLAYING → PAUSED → PLAYING → STOPPED

CoffeeMachine:
SELECTING → PAID → READY → DISPENSED
            ↓
        OUT_OF_INGREDIENT
```

#### Pattern Implementation

```java
public interface PlayerState {
    void play(VideoPlayer player);
    void pause(VideoPlayer player);
    void stop(VideoPlayer player);
}

// Context delegates to state
public void clickPlay() {
    state.play(this);
}

// State changes
public void changeState(PlayerState newState) {
    this.state = newState;
}
```

#### Use Cases
- ✓ Multiple behavioral states
- ✓ Complex state transitions
- ✓ State-specific actions
- ✓ Avoid long if-else chains

---

### 5. Strategy Pattern

**Purpose**: Encapsulate interchangeable algorithms  
**Frequency**: 5/5 projects (Most flexible)

#### Implementations

| Project | Strategy | Implementations | File |
|---------|----------|-----------------|------|
| Netflix | StreamingStrategy | BasicStreamingStrategy, StandardStreamingStrategy, PremiumStreamingStrategy | `streaming/*` |
| Spotify | StreamingStrategy | FreeUserStrategy, PremiumUserStrategy | `strategies/*` |
| PaymentSystem | PaymentProcessor | CreditCardProcessor, PayPalProcessor, UPIProcessor | `processors/*` |
| RateLimiter | RateLimiter | TokenBucketLimiter, SlidingWindowLogLimiter, FixedWindowLimiter | `model/*` |
| CoffeeMachine | (Implicit) | Decorator pattern for customization | - |

#### Strategy Selection

```java
// Netflix: Based on subscription
StreamingStrategy strategy = switch(subscription) {
    case BASIC -> new BasicStreamingStrategy();
    case STANDARD -> new StandardStreamingStrategy();
    case PREMIUM -> new PremiumStreamingStrategy();
};

// PaymentSystem: Based on payment method
PaymentProcessor processor = factory.createProcessor(paymentMethod);

// RateLimiter: Based on algorithm
RateLimiter limiter = factory.createTokenBucketLimiter(capacity, rate);
```

#### Benefits
- ✓ Runtime algorithm switching
- ✓ Encapsulates algorithms
- ✓ Reduces conditional logic
- ✓ Promotes extensibility

---

### 6. Observer Pattern

**Purpose**: Notify multiple objects of state changes  
**Frequency**: 2/5 projects

#### Implementations

| Project | Subject | Observers | File |
|---------|---------|-----------|------|
| Netflix | ContentSubject | User (ContentObserver) | `observer/*` |
| PaymentSystem | PaymentGatewayService | CustomerNotifier, MerchantNotifier | `observers/*` |

#### Observer Flow

```
Netflix Content Updates:
ContentSubject
    └─ notifyNewContent(content)
       ├─ notify User1 "New on Netflix: Inception"
       ├─ notify User2 "New on Netflix: Inception"
       └─ notify User3 "New on Netflix: Inception"

PaymentSystem Transactions:
PaymentGatewayService
    └─ notifyObservers(transaction)
       ├─ notify CustomerNotifier
       └─ notify MerchantNotifier
```

#### Implementation

```java
public interface PaymentObserver {
    void update(Transaction transaction);
}

public class PaymentGatewayService {
    private List<PaymentObserver> observers = new ArrayList<>();
    
    public void addObserver(PaymentObserver observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(Transaction transaction) {
        for (PaymentObserver observer : observers) {
            observer.update(transaction);
        }
    }
}
```

#### Use Cases
- ✓ Event notifications
- ✓ Loose coupling
- ✓ Multiple subscribers
- ✓ Real-time updates

---

### 7. Command Pattern

**Purpose**: Encapsulate requests as objects  
**Frequency**: 1/5 projects

#### Implementations

| Project | Command | Actions | File |
|---------|---------|---------|------|
| Spotify | Command | PlayCommand, PauseCommand, NextCommand, StopCommand | `commands/*` |

#### Command Pattern

```java
public interface Command {
    void execute();
}

// Concrete Commands
class PlayCommand implements Command {
    private Player player;
    
    public PlayCommand(Player player) {
        this.player = player;
    }
    
    public void execute() {
        player.play();
    }
}

// Usage
Command cmd = new PlayCommand(player);
cmd.execute();
```

#### Benefits
- ✓ Encapsulates requests
- ✓ Undo/Redo capability
- ✓ Queue commands
- ✓ Decouple sender and receiver

---

## 🏗️ Structural Patterns

### 8. Composite Pattern

**Purpose**: Treat individual objects and compositions uniformly  
**Frequency**: 1/5 projects

#### Implementations

| Project | Component | Leaf | Composite |
|---------|-----------|------|-----------|
| Netflix | Content | Movie, Episode | Series |

#### Composite Structure

```
Content (Interface)
├─ Movie (Leaf)
│  └─ getWatchableItems() → [this]
├─ Episode (Leaf)
│  └─ getWatchableItems() → [this]
└─ Series (Composite)
   └─ getWatchableItems() → [episode1, episode2, ...]
```

#### Implementation

```java
public interface Content {
    String getId();
    String getTitle();
    List<Content> getWatchableItems();
}

public class Movie implements Content {
    public List<Content> getWatchableItems() {
        return List.of(this);  // Single item
    }
}

public class Series implements Content {
    private List<Content> episodes = new ArrayList<>();
    
    public List<Content> getWatchableItems() {
        return new ArrayList<>(episodes);  // Multiple items
    }
}
```

#### Benefits
- ✓ Uniform treatment of objects
- ✓ Recursive composition
- ✓ Simplified client code
- ✓ Flexible hierarchies

---

### 9. Facade Pattern

**Purpose**: Provide simplified interface to complex subsystem  
**Frequency**: 1/5 projects

#### Implementations

| Project | Facade | Simplifies | File |
|---------|--------|-----------|------|
| CoffeeMachine | CoffeeVendingMachine | State machine + Inventory | `facade/CoffeeVendingMachine.java` |

#### Facade Pattern

```java
public class CoffeeVendingMachine {
    private VendingMachineState currentState;
    private Inventory inventory;
    private Coffee selectedCoffee;
    private int insertedMoney;
    
    // Simple interface for complex system
    public void selectCoffee(CoffeeType type, List<ToppingType> toppings) { ... }
    public void insertMoney(int amount) { ... }
    public void dispenseCoffee() { ... }
}
```

#### Benefits
- ✓ Hides complexity
- ✓ Simplified client code
- ✓ Decouples client from subsystem
- ✓ Easier testing

---

### 10. Decorator Pattern

**Purpose**: Add behavior to objects dynamically  
**Frequency**: 1/5 projects

#### Implementations

| Project | Component | Decorators | File |
|---------|-----------|-----------|------|
| CoffeeMachine | Coffee | CaramelSyrupDecorator, ExtraSugarDecorator | `decorator/*` |

#### Decorator Pattern

```java
public interface Coffee {
    int getPrice();
    int getCost();
}

public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

public class CaramelSyrupDecorator extends CoffeeDecorator {
    public int getPrice() {
        return coffee.getPrice() + 30;  // Add surcharge
    }
}

// Usage - Decorator stacking
Coffee coffee = new SimpleCoffee();
coffee = new CaramelSyrupDecorator(coffee);
coffee = new ExtraSugarDecorator(coffee);
```

#### Benefits
- ✓ Runtime behavior addition
- ✓ Flexible combinations
- ✓ Avoids class explosion
- ✓ Single responsibility

---

## 📊 Pattern Distribution

### By Project

```
Netflix (7 patterns):
├─ Singleton: NetflixSystem
├─ Factory: ContentFactory
├─ Composite: Content/Movie/Series
├─ State: VideoPlayer states
├─ Strategy: StreamingStrategy
├─ Observer: ContentSubject/User
└─ Builder: User.Builder

CoffeeMachine (4 patterns):
├─ Singleton: Inventory
├─ Facade: CoffeeVendingMachine
├─ Decorator: Coffee customization
└─ State: VendingMachine states

PaymentSystem (4 patterns):
├─ Singleton: PaymentGatewayService
├─ Factory: PaymentProcessorFactory
├─ Strategy: PaymentProcessor
└─ Observer: Payment notifications

Spotify (5 patterns):
├─ Singleton: MusicStreamingSystem
├─ Builder: User.Builder
├─ Command: Playback commands
├─ State: Player states
└─ Strategy: StreamingStrategy

RateLimiter (3 patterns):
├─ Factory: LimiterFactory
├─ Strategy: Rate limiting algorithms
└─ (Optional) Singleton: Factory instance
```

### By Category

```
Creational (3):
├─ Singleton: ●●●●●
├─ Factory: ●●●●
└─ Builder: ●●

Behavioral (4):
├─ State: ●●●
├─ Strategy: ●●●●●
├─ Observer: ●●
└─ Command: ●

Structural (3):
├─ Composite: ●
├─ Facade: ●
└─ Decorator: ●
```

---

## 🎓 Learning Paths

### Beginner Path (Simple Patterns)
1. **Singleton** → Single instance management
2. **Factory** → Object creation
3. **Builder** → Complex construction
4. **Decorator** → Runtime customization

### Intermediate Path (Behavioral Patterns)
1. **State** → State machine implementation
2. **Strategy** → Runtime algorithm selection
3. **Facade** → Simplifying complexity
4. **Observer** → Event notification

### Advanced Path (Complex Systems)
1. **Command** → Action encapsulation
2. **Composite** → Hierarchical structures
3. **Multi-pattern integration** → Netflix with 7 patterns
4. **Real-world systems** → Combined pattern usage

---

## 🔗 Pattern Relationships

```
Singleton
    ├─→ Often used with Factory
    └─→ Often used with Facade

Factory
    ├─→ Creates objects for Strategy
    ├─→ Creates objects for State
    └─→ Creates objects for Composite

Builder
    ├─→ Alternative to Factory for complex objects
    └─→ Used with Singleton for configuration

State
    ├─→ Works with Strategy for behavior
    └─→ Used in stateful systems

Strategy
    ├─→ Often created by Factory
    ├─→ Works with State
    └─→ Implemented in polymorphic way

Observer
    ├─→ Decouples subjects from observers
    └─→ Used for event-driven systems

Command
    ├─→ Encapsulates actions
    └─→ Enables undo/redo

Composite
    ├─→ Recursive structure
    └─→ Uniform treatment of parts

Facade
    ├─→ Simplifies subsystem
    └─→ Hides complexity

Decorator
    ├─→ Adds behavior dynamically
    └─→ Alternative to inheritance
```

---

## 📈 Pattern Complexity Matrix

```
              Low Complexity → High Complexity
Understand   Singleton, Factory              Command, Composite
Implement    Singleton, Factory              Command, Composite
Debug        Factory, Decorator              State, Observer
Extend       Singleton, Factory              Composite, Facade
```

---

## 🎯 Pattern Selection Guide

**When to use Singleton**: Single shared resource needed
**When to use Factory**: Object creation logic is complex
**When to use Builder**: Constructing complex objects
**When to use State**: Behavior depends on object state
**When to use Strategy**: Multiple algorithms for same task
**When to use Observer**: Notify multiple objects of changes
**When to use Command**: Encapsulate requests as objects
**When to use Composite**: Treat individual and group objects uniformly
**When to use Facade**: Simplify complex subsystem
**When to use Decorator**: Add behavior without subclassing

---

*Design Patterns Complete Index - Reference Guide*
*Last Updated: June 17, 2026*

