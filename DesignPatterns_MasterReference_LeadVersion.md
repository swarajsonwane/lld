# Design Patterns — Complete Interview Reference

> Lead-engineer version reconstructed from screenshots, corrected UML-style ASCII diagrams, interview notes, SOLID mappings, pattern-selection guide, and repo examples.

---

# Table of Contents

1. Singleton
2. Factory Method
3. Builder
4. Decorator
5. Strategy
6. State
7. Observer
8. Command
9. Composite
10. Template Method
11. Facade
12. Pattern Selection Cheat Sheet
13. Common Confusions
14. Interview Pro Tips

---

# 1. Singleton Pattern

## Category
Creational

## Real-Life Analogy
One president for a country.

## When to Use
- Shared resources
- Configuration
- Cache
- Inventory

## Structure

```text
+------------------+
|    Singleton     |
+------------------+
| - instance       |
+------------------+
| + getInstance()  |
+------------------+
```

---

# 2. Factory Method Pattern

## Category
Creational

## Real-Life Analogy
Pizza menu → kitchen decides what object to create.

## Structure

```text
            <<interface>>
                Product
                   ^
                   |
        +----------+----------+
        |          |          |
    ProductA   ProductB   ProductC

              +---------+
              | Factory |
              +---------+
              | create()|
              +---------+
```

---

# 3. Builder Pattern

## Category
Creational

## Real-Life Analogy
Custom burger order.

## Structure

```text
+-------------------+
|      Product      |
+-------------------+
         ^
         |
   creates
         |
+-------------------+
|      Builder      |
+-------------------+
| field1()          |
| field2()          |
| build()           |
+-------------------+
```

---

# 4. Decorator Pattern

## Category
Structural

## Real-Life Analogy
Gift wrapping layers.

## Structure

```text
                <<interface>>
                  Component
                + operation()
                       |
                 implements
          +------------+-------------+
          |                          |
+------------------+     +------------------------+
| ConcreteComponent|     | Abstract Decorator     |
| (the leaf)       |     | - wrapped : Component  |
+------------------+     | + operation()          |
                         +------------------------+
                                    |
                                  extends
                           +--------+--------+
                           |                 |
                    +-------------+   +-------------+
                    | DecoratorA  |   | DecoratorB  |
                    | +operation()|   | +operation()|
                    +-------------+   +-------------+
```

### Key Point
Decorator **adds** behavior by wrapping.

---

# 5. Strategy Pattern

## Category
Behavioral

## Real-Life Analogy
Google Maps routes.

## Structure

```text
            <<interface>>
               Strategy
             + execute()
                   ^
        +----------+----------+
        |                     |
 StrategyA              StrategyB

              Context
                 |
         delegates to Strategy
```

### Used In Repo
- PlaybackStrategy
- RecommendationStrategy
- PaymentProcessor
- RateLimiter

---

# 6. State Pattern

## Category
Behavioral

## Real-Life Analogy
Coffee vending machine.

## When to Use
- Behavior depends on state
- Complex transitions
- Avoid large if-else chains

## Structure

```text
+------------------+      has-a      +------------------+
|     Context      |---------------->|      State       |
| - state          |                 | + handle(ctx)    |
+------------------+                 +------------------+
                                             ^
                                  +----------+----------+
                                  |          |          |
                              StateA     StateB     StateC
```

## Example Flow

```text
Ready
  |
  v
Selecting
  |
  v
Paid
  |
  v
Dispensing
  |
  v
Ready
```

---

# 7. Observer Pattern

## Category
Behavioral

## Real-Life Analogy
YouTube subscriptions.

## Structure

```text
                 Subject
                    |
     +--------------+--------------+
     |              |              |
     v              v              v
 ObserverA     ObserverB     ObserverC
```

### Used In Repo
- PaymentGatewayService → CustomerNotifier, MerchantNotifier
- Artist → User subscribers

---

# 8. Command Pattern

## Category
Behavioral

## Real-Life Analogy
Restaurant order slip.

## Structure

```text
Invoker
   |
   v
Command
   |
   v
Receiver
```

### Spotify Example

```text
Button
   |
 PlayCommand
   |
 Player
```

### Benefits
- Undo
- Redo
- Queue
- History
- Replay

---

# 9. Composite Pattern

## Category
Structural

## Real-Life Analogy
File system.

## Structure

```text
                  Component
                      ^
                      |
          +-----------+-----------+
          |                       |
        Leaf                 Composite
                                |
                            children[]
                                |
                      +----+----+----+
                      |    |    |    |
                    Leaf Leaf Leaf Leaf
```

### Used In Repo

```text
Playable
 ├── Song
 ├── Album
 └── Playlist
```

---

# 10. Template Method Pattern

## Category
Behavioral

## Real-Life Analogy
Recipe template.

## Structure

```text
Template Method
 ├── Step 1 (fixed)
 ├── Step 2 (fixed)
 ├── Hook (override)
 └── Step 3 (fixed)
```

### Used In Repo
- Coffee.prepare()
- AbstractPaymentProcessor.processPayment()

---

# 11. Facade Pattern

## Category
Structural

## Real-Life Analogy
Hotel concierge.

## Structure

```text
             Client
                |
                v
            Facade
          /    |    \
         v     v     v
   ServiceA ServiceB ServiceC
```

### Key Idea
One simple API hides many subsystems.

---

# Pattern Selection Cheat Sheet

| Situation | Pattern |
|------------|------------|
| Only ONE instance should exist | Singleton |
| Create objects without exposing creation logic | Factory |
| Build complex objects step-by-step | Builder |
| Add behavior dynamically without subclassing | Decorator |
| Switch algorithms at runtime | Strategy |
| Object behaves differently based on internal state | State |
| Notify multiple objects when something happens | Observer |
| Encapsulate actions as objects | Command |
| Treat single objects and groups uniformly | Composite |
| Fixed algorithm skeleton | Template Method |
| Simplify complex subsystem | Facade |

---

# Common Confusions

## Strategy vs State

**Strategy**
- Client chooses algorithm
- Algorithm doesn't change itself

**State**
- Context transitions automatically
- State changes behavior

---

## Decorator vs Strategy

**Decorator**
- Wraps object
- Adds behavior
- Multiple decorators stack

**Strategy**
- Replaces behavior
- One active strategy

---

## Factory vs Builder

**Factory**
- Creates different object types

**Builder**
- Creates one type with many options

---

## Observer vs Command

**Observer**
- One event → many reactions

**Command**
- One action encapsulated as object

---

# SOLID Mapping

| Pattern | SOLID Principle |
|----------|----------|
| Factory | Open/Closed |
| Strategy | Open/Closed + Dependency Inversion |
| Observer | Single Responsibility |
| Decorator | Open/Closed |
| Facade | Interface Segregation |
| Builder | Single Responsibility |
| State | Open/Closed |

---

# Interview Pro Tips

1. Start with interfaces first.
2. Draw UML before code.
3. Explain WHY the pattern fits.
4. Mention extensibility.
5. Connect to SOLID.
6. Mention real production examples.

### Spring Boot Mapping

| Pattern | Spring Example |
|----------|----------|
| Singleton | Spring Bean |
| Factory | BeanFactory |
| Builder | Lombok @Builder |
| Strategy | PaymentProcessor |
| Observer | ApplicationEventPublisher |
| Command | Runnable / Executor |
| State | Order Workflow |
| Composite | File Tree |
| Decorator | Filters |
| Template Method | JdbcTemplate |
| Facade | Service Layer |

---

# Quick Decision Flow

```text
Need ONE instance?
 -> Singleton

Need object creation?
 -> Factory

Need many optional parameters?
 -> Builder

Need runtime behavior extension?
 -> Decorator

Need algorithm swap?
 -> Strategy

Need state transitions?
 -> State

Need notifications?
 -> Observer

Need undo/history?
 -> Command

Need tree hierarchy?
 -> Composite

Need fixed workflow?
 -> Template Method

Need simplified API?
 -> Facade
```
