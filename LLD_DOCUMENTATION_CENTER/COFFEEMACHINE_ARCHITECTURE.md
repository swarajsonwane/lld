# CoffeeMachine LLD - Architecture & Design

## System Overview

**Project**: Coffee Vending Machine System  
**Language**: Java 21  
**Patterns**: 4 (Singleton, Facade, Decorator, State)  
**Components**: 12+ Classes  
**Focus**: Real-world vending machine simulation with state management

---

## 🏗️ Architecture Diagram

### High-Level System

```
                    ┌──────────────────────────┐
                    │ CoffeeVendingMachineDemo │
                    │      (Main Entry)        │
                    └────────────┬─────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
        ┌───────────────────────┐  ┌─────────────────────┐
        │ CoffeeVendingMachine  │  │    Inventory        │
        │   (Facade)            │  │   (Singleton)       │
        │───────────────────────│  │─────────────────────│
        │ - currentState        │  │ - stocks[]          │
        │ - selectedCoffee      │  │ + addStock()        │
        │ - insertedMoney       │  │ + consumeIngredient │
        │ + selectCoffee()      │  │ + printInventory()  │
        │ + insertMoney()       │  │ + checkAvailable()  │
        │ + dispenseCoffee()    │  └─────────────────────┘
        │ - setState()          │
        └────────────┬──────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
   ┌──────────────┐       ┌──────────────────┐
   │ Coffee       │       │ VendingMachine   │
   │ (Interface)  │       │ State            │
   │──────────────│       │ (Interface)      │
   │ + getPrice() │       │──────────────────│
   │ + prepare()  │       │ + select()       │
   │ + getCost()  │       │ + insertMoney()  │
   └──────┬───────┘       │ + dispense()     │
          │               └────────┬─────────┘
          │                        │
   ┌──────┴──────┐    ┌───────────┼───────────────┬──────────────┐
   │             │    │           │               │              │
   ▼             ▼    ▼           ▼               ▼              ▼
┌─────────┐ ┌────────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
│SimpleCof│ │DecoratedCoffee │ │Selecting │ │  Paid    │ │   Ready      │
│ fee     │ │                │ │ State    │ │  State   │ │   State      │
└─────────┘ └────────────────┘ └──────────┘ └──────────┘ └──────────────┘
              │      │          
              ▼      ▼
        ┌─────────────────────┐   ┌──────────────────────┐
        │CaramelSyrupDecorator│   │ExtraSugarDecorator   │
        │ (Decorator)         │   │ (Decorator)          │
        └─────────────────────┘   └──────────────────────┘
```

---

## 🔄 State Machine Flow

```
         ┌─ User Session Start ─┐
         │                      ▼
    ┌────────────────┐   ┌──────────────┐
    │  SELECTING     │   │   INSERT     │
    │  SELECT STATE  │   │   MONEY      │
    │                │   │              │
    │ User selects   │   │ Money counter│
    │ coffee type    │   │ incremented  │
    └────────────────┘   └──────────────┘
         ▲                      │
         │                      │ (once price met)
         │ Insufficient │       ▼
         │ money        │   ┌──────────────┐
         └──────────────┼───│   PAID STATE │
                        │   │              │
                        │   │ Money ≥ Price
                        │   └──────────────┘
                        │         │
                        │ (sufficient funds met)
                        │         ▼
                        │   ┌──────────────┐
                        │   │  READY STATE │
                        │   │              │
                        │   │ Preparing    │
                        │   │ coffee...    │
                        │   └──────────────┘
                        │         │
                        │ (prepare complete)
                        │         ▼
                        │   ┌──────────────┐
                        └───│  DISPENSED   │
                            │  COFFEE      │
                            └──────────────┘
```

---

## 🏭 Component Interactions

### State Pattern Implementation

```
VendingMachine (Context)
    │
    ├─► SelectingState
    │   └─► selectCoffee()
    │       └─► Validates coffee type
    │
    ├─► PaidState
    │   └─► insertMoney()
    │       ├─► Adds to total
    │       └─► Checks if enough
    │
    ├─► ReadyState
    │   └─► dispenseCoffee()
    │       ├─► Gets coffee recipe
    │       ├─► Consumes ingredients
    │       └─► Dispenses coffee
    │
    └─► OutOfIngredientState
        └─► Handle insufficient ingredients
```

### Decorator Pattern - Coffee Customization

```
SimpleCoffee (Base)
    ├─► getPrice() = 50
    ├─► getCost() = 10
    ├─► getDuration() = 30
    │
    └─► Wrapped by Decorators
        │
        ├─► CaramelSyrupDecorator
        │   ├─► Adds +30 to price
        │   ├─► Adds +5 to cost
        │   └─► Adds +5 to duration
        │
        └─► ExtraSugarDecorator
            ├─► Adds +20 to price
            ├─► Adds +2 to cost
            └─► Adds +3 to duration
```

---

## 📊 Class Hierarchy

```
CoffeeVendingMachine (Facade)
├── Inventory (Singleton)
├── VendingMachineState (Interface)
│   ├── SelectingState
│   ├── PaidState
│   ├── ReadyState
│   └── OutOfIngredientState
├── Coffee (Interface)
│   ├── SimpleCoffee
│   └── CoffeeDecorator (Abstract)
│       ├── CaramelSyrupDecorator
│       └── ExtraSugarDecorator
└── Enums
    ├── CoffeeType
    ├── Ingredient
    └── ToppingType
```

---

## 🔄 Data Flow

### Purchase Flow

```
User Action                System Processing           State Change
─────────────             ──────────────────────      ────────────
1. selectCoffee()  ─────► Validate coffee type  ──►  SelectingState
                          Check ingredients
                          Set selected coffee

2. insertMoney()   ─────► Add to money counter  ──►  Decide state
                          Check if price met         (Paid/Selecting)

3. If sufficient  ─────► Prepare coffee        ──►  ReadyState
                          Get recipe               (preparing)
                          Consume ingredients

4. dispenseCoffee()─────► Dispense coffee      ──►  Session End
                          Calculate change
                          Display receipt
```

### Ingredient Tracking

```
Inventory (Singleton)
│
├─► COFFEE_BEANS (initial: 50 units)
├─► WATER (initial: 500 units)
├─► MILK (initial: 200 units)
├─► SUGAR (initial: 100 units)
└─► CARAMEL_SYRUP (initial: 50 units)

When Coffee Selected:
├─► Check availability
├─► Consume from inventory
└─► Update stock count
```

---

## 🎯 Design Patterns Used

### 1. Singleton Pattern
**Where**: Inventory, CoffeeVendingMachine  
**Why**: Single shared instance for inventory management  
**Implementation**: Double-checked locking

```java
private static volatile Inventory instance;
public static Inventory getInstance() {
    if (instance == null) {
        synchronized (Inventory.class) {
            if (instance == null) {
                instance = new Inventory();
            }
        }
    }
    return instance;
}
```

### 2. Facade Pattern
**Where**: CoffeeVendingMachine  
**Why**: Simplifies machine interface for clients  
**Provides**: selectCoffee, insertMoney, dispenseCoffee  
**Hides**: Complex state management

### 3. State Pattern
**Where**: VendingMachineState implementations  
**Why**: Different behavior based on machine state  
**States**: Selecting → Paid → Ready → Dispensed  
**Handles**: OutOfIngredient scenarios

### 4. Decorator Pattern
**Where**: CoffeeDecorator, Toppings  
**Why**: Add toppings dynamically to coffee  
**Decorators**: CaramelSyrup, ExtraSugar  
**Allows**: Flexible coffee customization

---

## 📋 Component Details

### Inventory (Singleton)
```
Responsibilities:
├─ Track ingredient stock
├─ Consume ingredients on purchase
├─ Check ingredient availability
├─ Refill ingredients
└─ Display inventory status
```

### CoffeeVendingMachine (Facade)
```
Responsibilities:
├─ Accept user selections
├─ Accept money input
├─ State management
├─ Inventory coordination
├─ Coffee preparation
└─ Dispensing logic
```

### VendingMachineState (Interface)
```
Implementations:
├─ SelectingState: Accept coffee selection
├─ PaidState: Monitor money insertion
├─ ReadyState: Prepare coffee
└─ OutOfIngredientState: Handle errors
```

### Coffee (Interface)
```
Implementations:
├─ SimpleCoffee: Base coffee
├─ DecoratedCoffee: With toppings
└─ Decorators:
   ├─ CaramelSyrupDecorator: Add caramel
   └─ ExtraSugarDecorator: Add sugar
```

---

## 🔗 Pattern Connections

```
Facade Pattern (CoffeeVendingMachine)
    ├─► Uses State Pattern for behavior
    │       └─► VendingMachineState interface
    │
    ├─► Uses Singleton (Inventory)
    │       └─► Consistent stock management
    │
    └─► Creates Coffee objects with Decorator Pattern
            └─► SimpleCoffee + Decorators
```

---

## 🚀 Execution Flow

```
START
  │
  ├─► Initialize Machine (Singleton)
  │   └─► Initialize Inventory (Singleton)
  │
  ├─► Setup inventory stock
  │   ├─ Coffee Beans: 50
  │   ├─ Water: 500
  │   ├─ Milk: 200
  │   ├─ Sugar: 100
  │   └─ Caramel Syrup: 50
  │
  ├─► Scenario 1: Successful Purchase
  │   ├─ Select Coffee (Latte)
  │   ├─ Insert Money
  │   ├─ Dispense Coffee
  │   └─ Update Inventory
  │
  ├─► Scenario 2: Insufficient Money
  │   ├─ Select Coffee (Cappuccino)
  │   ├─ Insert insufficient Money
  │   ├─ Request more money
  │   └─ Add more funds
  │
  ├─► Scenario 3: Coffee with Toppings
  │   ├─ Select Coffee with Caramel
  │   ├─ Decorator adds topping
  │   ├─ Insert Money
  │   └─ Dispense Decorated Coffee
  │
  └─► END (Display final inventory)
```

---

## 💡 Key Features

✓ **Realistic Vending Machine Behavior**
- State-based operation
- Money handling
- Change calculation

✓ **Ingredient Management**
- Track stock levels
- Prevent over-dispensing
- Handle out-of-ingredient scenarios

✓ **Coffee Customization**
- Add multiple toppings
- Dynamic pricing based on toppings
- Recipe customization

✓ **Error Handling**
- Insufficient money detection
- Ingredient shortage handling
- Invalid selection management

---

## 📊 Data Model

### CoffeeType Enum
```
ESPRESSO   (price: 50, duration: 15s)
LATTE      (price: 70, duration: 25s)
CAPPUCCINO (price: 70, duration: 25s)
AMERICANO  (price: 50, duration: 20s)
MOCHA      (price: 80, duration: 30s)
```

### Ingredient Enum
```
COFFEE_BEANS (50 units)
WATER (500 units)
MILK (200 units)
SUGAR (100 units)
CARAMEL_SYRUP (50 units)
```

### ToppingType Enum
```
CARAMEL_SYRUP
EXTRA_SUGAR
CHOCOLATE
CINNAMON
```

---

## 🎓 Learning Outcomes

From this project, you'll understand:

1. **State Pattern**: How to handle different states in a system
2. **Facade Pattern**: Simplifying complex subsystems
3. **Singleton Pattern**: Managing shared resources
4. **Decorator Pattern**: Runtime behavior extension
5. **Real-World Constraints**: Money, inventory, state validation
6. **Error Handling**: OutOfIngredient scenarios

---

## 🔍 Advanced Features

✓ **State Validation**: Prevents invalid transitions  
✓ **Money Calculation**: Change calculation logic  
✓ **Ingredient Consumption**: Recipe-based consumption  
✓ **Multiple Toppings**: Decorator stacking  
✓ **Inventory Persistence**: Stock tracking  

---

## 📈 Complexity Analysis

| Operation | Complexity | Time |
|-----------|-----------|------|
| Select Coffee | O(1) | Instant |
| Insert Money | O(1) | Instant |
| Check Inventory | O(1) | Instant |
| Prepare Coffee | O(1) | Simulated (15-30s) |
| Dispense | O(1) | Instant |

---

## 🎯 Use Cases

1. **Vending Machine Simulation** - Test machine behavior
2. **Inventory Management** - Stock tracking
3. **State Management Learning** - Understand state patterns
4. **Decorator Pattern Study** - Learn customization
5. **Real-world System Modeling** - Practical application

---

*CoffeeMachine LLD Architecture Document - Complete Reference*
*Last Updated: June 17, 2026*

