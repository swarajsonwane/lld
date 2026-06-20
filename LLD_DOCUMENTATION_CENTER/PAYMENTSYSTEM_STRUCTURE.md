# PaymentSystem LLD - Project Structure

## 📁 Directory Layout

```
PaymentSystem/
├── PaymentSystem.iml                    # IntelliJ configuration (Java 21)
├── build.sh                             # Build script
├── run.sh                               # Run script
└── src/
    ├── PaymentGatewayDemo.java          # Main demo class
    ├── PaymentGatewayService.java       # Singleton + Facade
    │
    ├── enums/
    │   ├── PaymentMethod.java           # CREDIT_CARD, PAYPAL, UPI
    │   └── PaymentStatus.java           # SUCCESS, FAILED, PENDING
    │
    ├── factory/
    │   └── PaymentProcessorFactory.java # Creates processors
    │
    ├── models/
    │   ├── PaymentRequest.java          # Request with Builder
    │   ├── PaymentResponse.java         # Response object
    │   └── Transaction.java             # Transaction record
    │
    ├── observers/
    │   ├── PaymentObserver.java         # Observer interface
    │   ├── CustomerNotifier.java        # Customer notifications
    │   └── MerchantNotifier.java        # Merchant notifications
    │
    └── processors/
        ├── PaymentProcessor.java        # Strategy interface
        ├── AbstractPaymentProcessor.java # Abstract base
        ├── CreditCardProcessor.java     # Credit card processing
        ├── PayPalProcessor.java         # PayPal processing
        └── UPIProcessor.java            # UPI processing
```

---

## 📄 File Descriptions

### Main Class

#### `PaymentGatewayDemo.java`
**Purpose**: Entry point and test scenarios  
**Responsibilities**:
- Initialize payment gateway
- Register observers
- Execute payment scenarios
- Display results

---

### Gateway Service

#### `PaymentGatewayService.java` (Singleton + Facade)
```
Core payment gateway system

Responsibilities:
├─ Singleton instance management
├─ Accept payment requests
├─ Route to processors
├─ Manage observers
└─ Orchestrate transactions

Key Fields:
├─ private static volatile instance
├─ private Map<PaymentMethod, PaymentProcessor> processors
├─ private List<PaymentObserver> observers
└─ private PaymentProcessorFactory factory

Key Methods:
├─ getInstance()
├─ processPayment(PaymentRequest)
├─ addObserver(PaymentObserver)
├─ removeObserver(PaymentObserver)
├─ notifyObservers(Transaction)
└─ getPaymentHistory()

Thread Safety:
└─ Double-checked locking for instance creation
```

---

### Enumerations (`enums/`)

#### `PaymentMethod.java`
```java
public enum PaymentMethod {
    CREDIT_CARD,
    PAYPAL,
    UPI
}
```

**Usage**: Select which processor to use for payment

#### `PaymentStatus.java`
```java
public enum PaymentStatus {
    SUCCESS,
    FAILED,
    PENDING
}
```

**Usage**: Track payment outcome

---

### Factory (`factory/`)

#### `PaymentProcessorFactory.java`
```
Factory for creating payment processors

Responsibilities:
├─ Create appropriate processor
├─ Cache processor instances (optional)
└─ Handle unknown payment methods

Method:
└─ createProcessor(PaymentMethod) → PaymentProcessor

Implementation:
├─ Use switch expression for selection
├─ Validate payment method
└─ Return instance of specific processor
```

---

### Models (`models/`)

#### `PaymentRequest.java`
```java
public class PaymentRequest {
    private String payerId;
    private double amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private Map<String, String> paymentDetails;
    
    // Builder Pattern
    public static class Builder {
        // setters return Builder for chaining
        public PaymentRequest build()
    }
}
```

**Fields**:
- `payerId`: Unique payer identifier
- `amount`: Payment amount
- `currency`: Currency code (INR, USD, etc.)
- `paymentMethod`: Which method to use
- `paymentDetails`: Method-specific details

#### `PaymentResponse.java`
```
Response object returned from payment processing

Fields:
├─ paymentId: Unique payment identifier
├─ status: PaymentStatus (SUCCESS/FAILED)
├─ transactionId: Transaction reference
├─ amount: Processed amount
├─ currency: Currency used
├─ timestamp: Processing time
└─ message: Result message
```

#### `Transaction.java`
```
Record of a transaction

Fields:
├─ id: Unique transaction ID
├─ paymentId: Link to payment
├─ status: PaymentStatus
├─ processor: Processor used
├─ timestamp: When processed
├─ amount: Transaction amount
└─ details: Additional info
```

---

### Observers (`observers/`)

#### `PaymentObserver.java` (Interface)
```java
public interface PaymentObserver {
    void update(Transaction transaction);
}
```

**Purpose**: Observer pattern for payment notifications

#### `CustomerNotifier.java` (Implementation)
```
Notifies customer of payment status

Responsibilities:
├─ Send payment confirmation
├─ Notify of failures
├─ Include transaction details
└─ Format customer-friendly messages

Method:
└─ update(Transaction)
    └─ System.out.println("Customer: ...")
```

**Notifications Include**:
- Payment status (success/failure)
- Transaction ID
- Amount
- Timestamp
- Payment method

#### `MerchantNotifier.java` (Implementation)
```
Notifies merchant of payment received

Responsibilities:
├─ Confirm payment receipt
├─ Include merchant details
├─ Track revenue
└─ Format merchant-specific messages

Method:
└─ update(Transaction)
    └─ System.out.println("Merchant: ...")
```

**Notifications Include**:
- Payment confirmed
- Transaction ID
- Amount received
- Payer info
- Settlement details

---

### Processors (`processors/`)

#### `PaymentProcessor.java` (Strategy Interface)
```java
public interface PaymentProcessor {
    PaymentResponse process(PaymentRequest request);
}
```

**Implementations**:
- CreditCardProcessor
- PayPalProcessor
- UPIProcessor

#### `AbstractPaymentProcessor.java`
```
Base class for all processors

Common Responsibilities:
├─ Validate request
├─ Log transaction
├─ Handle errors
└─ Generate response

Template Methods:
├─ validate(PaymentRequest)
├─ execute(PaymentRequest)
├─ updateStatus(Transaction)
└─ generateResponse(...)

Subclasses Override:
└─ processSpecific(PaymentRequest)
```

#### `CreditCardProcessor.java`
```
Handles credit card payments

Process:
├─ Validate card number
├─ Check expiry
├─ Verify CVV
├─ Check fraud
├─ Process transaction
└─ Return response

Specific Logic:
├─ Card validation
├─ Bank communication (simulated)
├─ 3D Secure check (simulated)
└─ Settlement
```

#### `PayPalProcessor.java`
```
Handles PayPal payments

Process:
├─ Validate PayPal account
├─ Check fund availability
├─ OAuth verification (simulated)
├─ Process transaction
└─ Return response

Specific Logic:
├─ PayPal API call (simulated)
├─ User authentication
├─ Fund transfer
└─ Confirmation
```

#### `UPIProcessor.java`
```
Handles UPI payments

Process:
├─ Validate UPI ID
├─ Check account status
├─ OTP verification (simulated)
├─ Process transaction
└─ Return response

Specific Logic:
├─ UPI ID validation
├─ Bank routing
├─ OTP confirmation
└─ Settlement
```

---

## 🔄 Method Flow

### Payment Processing
```
PaymentGatewayService.processPayment()
    ├─► Create PaymentResponse
    ├─► Get processor via Factory
    │   └─► Based on PaymentMethod
    ├─► Call processor.process()
    │   └─► AbstractPaymentProcessor.process()
    │       ├─ Validate request
    │       ├─ Execute payment
    │       └─ Generate response
    ├─► Create Transaction record
    ├─► Notify observers
    │   ├─ CustomerNotifier.update()
    │   └─ MerchantNotifier.update()
    └─► Return PaymentResponse
```

---

## 💾 Data Flow

### Request Creation (Builder Pattern)
```
PaymentRequest.Builder builder
    .payerId("U-123")
    .amount(150.75)
    .currency("INR")
    .paymentMethod(PaymentMethod.CREDIT_CARD)
    .paymentDetails(cardDetailsMap)
    .build()
```

### Payment Processing
```
Request
    │
    ├─► Gateway receives
    ├─► Factory creates processor
    ├─► Processor validates & processes
    ├─► Response created
    ├─► Transaction recorded
    ├─► Observers notified
    └─► Response returned
```

---

## 📊 Class Relationships

```
PaymentGatewayDemo
    │
    ├─ PaymentGatewayService (Singleton)
    │   ├─ PaymentProcessorFactory
    │   ├─ Map<PaymentMethod, PaymentProcessor>
    │   └─ List<PaymentObserver>
    │       ├─ CustomerNotifier
    │       └─ MerchantNotifier
    │
    ├─ PaymentRequest.Builder
    │   └─ PaymentRequest
    │       └─ PaymentMethod (Enum)
    │
    └─ PaymentProcessor (Interface)
        ├─ AbstractPaymentProcessor (Abstract)
        │   ├─ CreditCardProcessor
        │   ├─ PayPalProcessor
        │   └─ UPIProcessor
        │
        └─ PaymentResponse
            ├─ PaymentStatus (Enum)
            └─ Transaction
```

---

## 🎯 Validation Rules

### Payment Request Validation
```
├─ payerId must not be empty
├─ amount must be > 0
├─ currency must be valid ISO code
├─ paymentMethod must be supported
└─ paymentDetails must be complete for method
```

### Processor-Specific Validation
```
CreditCard:
├─ Card number 13-19 digits
├─ Expiry not passed
└─ CVV 3-4 digits

PayPal:
├─ Email format valid
└─ Account exists

UPI:
├─ UPI ID format valid
└─ Account active
```

---

## 📋 Test Scenarios

### Scenario 1: Successful Credit Card
```
Request:
  Amount: 150.75
  Method: CREDIT_CARD
  Card: 1234567890123456

Flow:
  1. Create request
  2. Route to CreditCardProcessor
  3. Validate card
  4. Process payment
  5. Success response
  6. Notify both observers
```

### Scenario 2: Failed PayPal
```
Request:
  Amount: 500.00
  Method: PAYPAL
  Email: test@email.com

Flow:
  1. Create request
  2. Route to PayPalProcessor
  3. Check funds
  4. Insufficient (fails)
  5. Failure response
  6. Notify observers of failure
```

### Scenario 3: UPI Payment
```
Request:
  Amount: 99.99
  Method: UPI
  UPI: user@okhdfcbank

Flow:
  1. Create request
  2. Route to UPIProcessor
  3. Validate UPI
  4. Verify OTP (simulated)
  5. Process payment
  6. Success response
  7. Notify observers
```

---

## 🚀 Build & Run

```bash
# Build
javac -d out/production/PaymentSystem $(find src -name "*.java" -type f)

# Run
java -cp out/production/PaymentSystem PaymentGatewayDemo

# Using scripts
bash build.sh
bash run.sh
```

---

## 📈 Statistics

| Metric | Count |
|--------|-------|
| Total Files | 12+ |
| Classes | 11+ |
| Interfaces | 3 |
| Enums | 2 |
| Design Patterns | 4 |
| Payment Methods | 3 |

---

## 🎓 Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| Singleton | PaymentGatewayService | Single gateway |
| Factory | PaymentProcessorFactory | Processor creation |
| Strategy | PaymentProcessor | Payment processing |
| Observer | PaymentObserver | Notifications |
| Builder | PaymentRequest | Object construction |

---

## 🔗 Extension Points

1. **Add payment method** → Create new Processor, update Enum
2. **Add notification channel** → Implement PaymentObserver
3. **Add retry logic** → Modify AbstractPaymentProcessor
4. **Add fraud detection** → Add to validation
5. **Add audit logging** → Implement new Observer

---

*PaymentSystem Project Structure - Complete Guide*
*Last Updated: June 17, 2026*

