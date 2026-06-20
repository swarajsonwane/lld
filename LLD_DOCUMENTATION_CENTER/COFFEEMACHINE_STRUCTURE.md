# CoffeeMachine LLD - Project Structure

## 📁 Directory Layout

```
CoffeeMachine/
├── CoffeeMachine.iml                    # IntelliJ configuration (Java 21)
├── CoffeeVendingMachineDemo.java        # Main demo class
├── build.sh                              # Build script
├── run.sh                                # Run script
└── src/ (or root level files for simple structure)
    ├── decorator/                        # Decorator Pattern
    │   ├── CaramelSyrupDecorator.java
    │   ├── CoffeeDecorator.java         # Abstract decorator
    │   ├── Coffee.java                  # Interface
    │   └── ExtraSugarDecorator.java
    │
    ├── enums/                            # Enumerations
    │   ├── CoffeeType.java
    │   ├── Ingredient.java
    │   └── ToppingType.java
    │
    ├── facade/                           # Facade Pattern
    │   └── CoffeeVendingMachine.java
    │
    ├── factory/                          # Factory (Optional)
    │   └── CoffeeFactory.java
    │
    ├── singleton/                        # Singleton Pattern
    │   └── Inventory.java
    │
    └── state/                            # State Pattern
        ├── VendingMachineState.java     # Interface
        ├── SelectingState.java
        ├── PaidState.java
        ├── ReadyState.java
        ├── OutOfIngredientState.java
        └── [additional states if needed]
```

---

## 📄 File Descriptions

### Main Class

#### `CoffeeVendingMachineDemo.java`
**Purpose**: Entry point for the coffee vending machine system  
**Responsibilities**:
- Initialize machine and inventory
- Simulate purchase scenarios
- Display test results

**Key Methods**:
```java
public static void main(String[] args) {
    // Setup and scenario execution
}
```

---

### Decorator Pattern (`decorator/`)

#### `Coffee.java` (Interface)
```
Defines the component interface for the Decorator pattern
Methods:
├─ getPrice() → int
├─ getCost() → int
├─ getDuration() → int
└─ prepare() → void
```

#### `CoffeeDecorator.java` (Abstract)
```
Abstract decorator base class
Extends: Coffee
Contains: Wrapped coffee instance
Methods:
├─ abstract decorated methods
└─ abstract implementation in subclasses
```

#### `CaramelSyrupDecorator.java`
```
Concrete decorator for Caramel Syrup topping
Adds:
├─ +30 to price
├─ +5 to cost
├─ +5 to duration
└─ Caramel flavor to coffee
```

#### `ExtraSugarDecorator.java`
```
Concrete decorator for Extra Sugar topping
Adds:
├─ +20 to price
├─ +2 to cost
├─ +3 to duration
└─ Extra sweetness to coffee
```

---

### Enumerations (`enums/`)

#### `CoffeeType.java`
```java
public enum CoffeeType {
    ESPRESSO(50, 15),      // price, duration
    LATTE(70, 25),
    CAPPUCCINO(70, 25),
    AMERICANO(50, 20),
    MOCHA(80, 30)
}
```

#### `Ingredient.java`
```java
public enum Ingredient {
    COFFEE_BEANS,
    WATER,
    MILK,
    SUGAR,
    CARAMEL_SYRUP
}
```

#### `ToppingType.java`
```java
public enum ToppingType {
    CARAMEL_SYRUP,
    EXTRA_SUGAR,
    CHOCOLATE,
    CINNAMON
}
```

---

### Facade Pattern (`facade/`)

#### `CoffeeVendingMachine.java`
```
Facade providing simplified interface
Implements: Singleton + Facade patterns

Key Responsibilities:
├─ selectCoffee(CoffeeType, List<ToppingType>)
├─ insertMoney(int amount)
├─ dispenseCoffee()
├─ getStatus()
└─ handleState transitions

Internal Components:
├─ currentState: VendingMachineState
├─ selectedCoffee: Coffee
├─ insertedMoney: int
├─ inventory: Inventory
└─ stateManager: StateManager

Features:
├─ Money validation
├─ Ingredient checking
├─ Change calculation
└─ Receipt generation
```

---

### Singleton Pattern (`singleton/`)

#### `Inventory.java`
```
Manages coffee ingredients and stock

Singleton Implementation:
├─ private static Inventory instance
├─ getInstance() method
└─ Double-checked locking

Data Structures:
├─ Map<Ingredient, Integer> stocks

Key Methods:
├─ addStock(Ingredient, int quantity)
├─ consumeIngredient(Ingredient, int quantity)
├─ checkAvailability(Recipe)
├─ isLowStock(Ingredient)
├─ refillIngredient(Ingredient)
└─ printInventory()

Stock Levels:
├─ COFFEE_BEANS: 50 units
├─ WATER: 500 units
├─ MILK: 200 units
├─ SUGAR: 100 units
└─ CARAMEL_SYRUP: 50 units
```

---

### State Pattern (`state/`)

#### `VendingMachineState.java` (Interface)
```java
public interface VendingMachineState {
    void selectCoffee(CoffeeVendingMachine machine, Coffee coffee);
    void insertMoney(CoffeeVendingMachine machine, int amount);
    void dispenseCoffee(CoffeeVendingMachine machine);
}
```

#### `SelectingState.java`
```
Initial state when user starts interaction

Responsibilities:
├─ Accept coffee selection
├─ Validate coffee type
├─ Check ingredient availability
├─ Transition to PaidState on money insertion

Methods:
├─ selectCoffee() → Sets selected coffee
├─ insertMoney() → Starts payment, transitions to PaidState
└─ dispenseCoffee() → Reject (Invalid operation)
```

#### `PaidState.java`
```
Money accumulation state

Responsibilities:
├─ Accept money insertion
├─ Accumulate total money
├─ Check if price is met
├─ Handle insufficient funds

Methods:
├─ selectCoffee() → Reject/Reset selection
├─ insertMoney() → Add to total, check price
└─ dispenseCoffee() → Prepare coffee if funds sufficient

Transition Logic:
├─ If money < price → Stay in PaidState
└─ If money ≥ price → Transition to ReadyState
```

#### `ReadyState.java`
```
Coffee preparation state

Responsibilities:
├─ Prepare selected coffee
├─ Consume ingredients
├─ Calculate and dispense change
├─ Generate receipt

Methods:
├─ selectCoffee() → Reject (Already dispensing)
├─ insertMoney() → Reject (Already ready)
└─ dispenseCoffee() → Dispense and return to SelectingState

Process:
├─ Get coffee recipe
├─ Verify ingredients available
├─ Consume from inventory
├─ Simulate preparation time
├─ Calculate change
└─ Display receipt
```

#### `OutOfIngredientState.java`
```
Error state when ingredients insufficient

Responsibilities:
├─ Reject coffee dispensing
├─ Notify user of shortage
├─ Suggest alternatives
├─ Return deposited money

Methods:
├─ selectCoffee() → Suggest alternatives
├─ insertMoney() → Reject (Out of ingredients)
└─ dispenseCoffee() → Return money, reject dispense
```

---

## 🔄 State Transitions

```
SelectingState
    ↓ (insertMoney)
PaidState
    ↓ (money ≥ price)
ReadyState
    ↓ (dispenseCoffee)
SelectingState (cycle repeats)

Alternative:
PaidState
    ↓ (insufficient ingredients)
OutOfIngredientState
    ↓ (returnMoney)
SelectingState
```

---

## 📊 Coffee Recipe Structure

Each CoffeeType has an associated recipe:

```
Recipe {
    ingredients: Map<Ingredient, quantity>
    duration: int (seconds)
    price: int (currency units)
}

Examples:
ESPRESSO {
    COFFEE_BEANS: 2
    WATER: 1
    SUGAR: 0
    Duration: 15s
    Price: 50
}

LATTE {
    COFFEE_BEANS: 1
    WATER: 1
    MILK: 2
    SUGAR: 1
    Duration: 25s
    Price: 70
}
```

---

## 💾 Data Model

### Money Handling
```
insertedMoney: int          // Total money inserted
selectedCoffee.getPrice()  // Coffee cost
change = insertedMoney - selectedCoffee.getPrice()
```

### Ingredient Consumption
```
When dispensing coffee:
For each ingredient in recipe:
    inventory.consumeIngredient(ingredient, quantity)
```

### Decorator Stacking
```
Base: SimpleCoffee(price: 50)
    ↓
+ CaramelSyrupDecorator(adds +30)
    ↓
+ ExtraSugarDecorator(adds +20)
    ↓
Final Coffee(price: 100)
```

---

## 🎯 Class Dependencies

```
CoffeeVendingMachineDemo
    ├─ CoffeeVendingMachine (Facade)
    │   ├─ VendingMachineState (Interface)
    │   │   ├─ SelectingState
    │   │   ├─ PaidState
    │   │   ├─ ReadyState
    │   │   └─ OutOfIngredientState
    │   ├─ Coffee (Interface)
    │   │   ├─ SimpleCoffee
    │   │   └─ CoffeeDecorator (Abstract)
    │   │       ├─ CaramelSyrupDecorator
    │   │       └─ ExtraSugarDecorator
    │   └─ Inventory (Singleton)
    │       ├─ Ingredient (Enum)
    │       └─ Stock Map
    │
    └─ Enums
        ├─ CoffeeType
        ├─ Ingredient
        └─ ToppingType
```

---

## 🚀 Build & Run

### Build Command
```bash
javac -d out/production/CoffeeMachine $(find . -name "*.java" -type f)
```

### Run Command
```bash
java -cp out/production/CoffeeMachine CoffeeVendingMachineDemo
```

### Using Scripts
```bash
bash build.sh
bash run.sh
```

---

## 📋 Test Scenarios

### Scenario 1: Successful Purchase
```
1. Select LATTE coffee
2. Insert 200 (price: 70)
3. Insert 50 (total: 250)
4. Dispense coffee
5. Receive change: 180
```

### Scenario 2: Insufficient Funds
```
1. Select CAPPUCCINO (price: 70)
2. Insert 50
3. Display "Insufficient funds, need 20 more"
4. Insert 20
5. Dispense coffee
```

### Scenario 3: Decorated Coffee
```
1. Select MOCHA with CARAMEL_SYRUP
2. Price: 80 (MOCHA) + 30 (CARAMEL) = 110
3. Insert 120
4. Dispense decorated coffee
5. Change: 10
```

### Scenario 4: Out of Ingredient
```
1. Select LATTE
2. Inventory lacks MILK
3. Display OutOfIngredientState
4. Suggest ESPRESSO
5. Return money
```

---

## 📈 Statistics

| Metric | Count |
|--------|-------|
| Total Files | 15+ |
| Classes | 10+ |
| Interfaces | 2 |
| Enums | 3 |
| Design Patterns | 4 |
| Coffee Types | 5 |
| Ingredients | 5 |
| Toppings | 4 |

---

## 🎓 Patterns Overview

| Pattern | File | Purpose |
|---------|------|---------|
| Singleton | `Inventory.java` | Single shared inventory |
| Facade | `CoffeeVendingMachine.java` | Simplified interface |
| State | `state/*` | State-based behavior |
| Decorator | `decorator/*` | Coffee customization |

---

## 🔗 Key Methods Summary

### CoffeeVendingMachine
```java
public void selectCoffee(CoffeeType type, List<ToppingType> toppings)
public void insertMoney(int amount)
public void dispenseCoffee()
public String getStatus()
```

### Inventory
```java
public static Inventory getInstance()
public void addStock(Ingredient ingredient, int quantity)
public void consumeIngredient(Ingredient ingredient, int quantity)
public boolean checkAvailability(Map<Ingredient, Integer> recipe)
public void printInventory()
```

### VendingMachineState
```java
void selectCoffee(CoffeeVendingMachine machine, Coffee coffee)
void insertMoney(CoffeeVendingMachine machine, int amount)
void dispenseCoffee(CoffeeVendingMachine machine)
```

---

## 💡 Extension Points

1. **Add new coffee types** → Update CoffeeType enum
2. **Add new toppings** → Create new Decorator
3. **Add payment methods** → Extend state logic
4. **Add loyalty program** → Extend CoffeeVendingMachine
5. **Add transaction logging** → Create Logger component

---

*CoffeeMachine Project Structure - Complete Guide*
*Last Updated: June 17, 2026*

