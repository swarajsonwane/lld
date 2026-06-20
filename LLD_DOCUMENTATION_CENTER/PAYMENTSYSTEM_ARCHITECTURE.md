# PaymentSystem LLD - Architecture & Design

## System Overview

**Project**: Payment Gateway System  
**Language**: Java 21  
**Patterns**: 4 (Singleton, Factory, Strategy, Observer)  
**Components**: 15+ Classes  
**Focus**: Multi-gateway payment processing with transaction notifications

---

## 🏗️ Architecture Diagram

### High-Level System

```
                    ┌──────────────────────────┐
                    │ PaymentGatewayDemo       │
                    │   (Main Entry)           │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────────┐
                    │ PaymentGatewayService       │
                    │ (Singleton + Facade)        │
                    │─────────────────────────────│
                    │ - processors (Map)          │
                    │ - observers (List)          │
                    │ + processPayment()          │
                    │ + addObserver()             │
                    │ + notifyObservers()         │
                    └────────────┬────────────────┘
                                 │
                ┌────────────────┼────────────────┐
                │                │                │
                ▼                ▼                ▼
        ┌───────────────┐  ┌──────────────┐  ┌───────────┐
        │PaymentProcessor   │PaymentFactory│  │Observer   │
        │(Strategy)     │  │(Factory)     │  │Interface  │
        │───────────────│  │──────────────│  │───────────│
        │- process()   │  │+ create()    │  │+ update() │
        └───────┬───────┘  └──────────────┘  └───────┬───┘
                │                                    │
    ┌───────────┼───────────┐           ┌───────────┴──────────┐
    │           │           │           │                      │
    ▼           ▼           ▼           ▼                      ▼
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐ ┌─────────────┐
│CreditCard│ │PayPal    │ │UPI       │ │CustomerNot │ │MerchantNot  │
│Processor │ │Processor │ │Processor │ │ifier       │ │ifier        │
└──────────┘ └──────────┘ └──────────┘ └────────────┘ └─────────────┘

Abstract Layer:
AbstractPaymentProcessor (Base class for all processors)
```

---

## 🔄 Payment Processing Flow

```
                User Request
                     │
                     ▼
        PaymentGatewayService.processPayment()
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
    Validate   Create   Route to
    Request    PaymentReq Processor
        │            │
        └────────────┴────────────┐
                                   ▼
                        PaymentFactory
                        (Create Processor)
                                   │
                ┌──────────────────┼──────────────────┐
                │                  │                  │
        CreditCard    PayPalProcessor    UPI Processor
        Processor
                │                  │                  │
                └──────────────────┴──────────────────┘
                                   │
                        ┌───────────▼───────────┐
                        │ Abstract Processor    │
                        │ execute()             │
                        ├─ Validate funds      │
                        ├─ Process transaction │
                        ├─ Update status       │
                        └─ Create response     │
                                   │
                                   ▼
                    ┌──────────────────────────┐
                    │ PaymentResponse         │
                    │ - status (SUCCESS/FAIL) │
                    │ - transactionId         │
                    │ - timestamp             │
                    └──────────────────────────┘
                                   │
                                   ▼
                    ┌──────────────────────────┐
                    │ Notify Observers         │
                    ├─ CustomerNotifier       │
                    └─ MerchantNotifier       │
                                   │
                                   ▼
                        Return to Client
```

---

## 💾 Data Models

### Payment Request
```
PaymentRequest {
    payerId: String
    amount: double
    currency: String
    paymentMethod: PaymentMethod
    paymentDetails: Map<String, String>
    
    Builder pattern for construction:
    new PaymentRequest.Builder()
        .payerId("U-123")
        .amount(150.75)
        .currency("INR")
        .paymentMethod(PaymentMethod.CREDIT_CARD)
        .paymentDetails(cardDetails)
        .build()
}
```

### Payment Response
```
PaymentResponse {
    paymentId: String
    status: PaymentStatus (SUCCESS/FAILED/PENDING)
    transactionId: String
    amount: double
    currency: String
    timestamp: LocalDateTime
    message: String
}
```

### Transaction Record
```
Transaction {
    id: String
    paymentId: String
    status: PaymentStatus
    processor: String
    timestamp: LocalDateTime
    amount: double
}
```

---

## 🎯 Design Patterns Used

### 1. Singleton Pattern
**Where**: PaymentGatewayService  
**Why**: Single payment gateway instance  
**Implementation**: Thread-safe double-checked locking

```java
private static volatile PaymentGatewayService instance;
public static PaymentGatewayService getInstance() {
    if (instance == null) {
        synchronized (PaymentGatewayService.class) {
            if (instance == null) {
                instance = new PaymentGatewayService();
            }
        }
    }
    return instance;
}
```

### 2. Factory Pattern
**Where**: PaymentProcessorFactory  
**Why**: Create appropriate processor based on payment method  
**Advantages**: Decouples processor creation from usage

```java
public PaymentProcessor createProcessor(PaymentMethod method) {
    return switch(method) {
        case CREDIT_CARD -> new CreditCardProcessor();
        case PAYPAL -> new PayPalProcessor();
        case UPI -> new UPIProcessor();
    };
}
```

### 3. Strategy Pattern
**Where**: PaymentProcessor interface & implementations  
**Why**: Different processing logic per payment method  
**Implementations**: CreditCard, PayPal, UPI

```java
public interface PaymentProcessor {
    PaymentResponse process(PaymentRequest request);
}
```

### 4. Observer Pattern
**Where**: PaymentObserver interface & implementations  
**Why**: Notify stakeholders of payment events  
**Observers**: CustomerNotifier, MerchantNotifier

```java
public interface PaymentObserver {
    void update(Transaction transaction);
}
```

---

## 🏭 Component Structure

### PaymentGatewayService (Singleton + Facade)
```
Responsibilities:
├─ Accept payment requests
├─ Route to appropriate processor
├─ Manage observers
├─ Notify on transaction completion
└─ Return payment response

Methods:
├─ getInstance() → Single instance
├─ processPayment(PaymentRequest)
├─ addObserver(PaymentObserver)
├─ removeObserver(PaymentObserver)
└─ notifyObservers(Transaction)
```

### PaymentProcessorFactory (Factory)
```
Responsibilities:
├─ Create appropriate processor
├─ Handle unknown payment methods
└─ Manage processor instances

Methods:
└─ createProcessor(PaymentMethod)
```

### PaymentProcessor Interface (Strategy)
```
Implementations:
├─ CreditCardProcessor
│  └─ Process credit card transactions
├─ PayPalProcessor
│  └─ Process PayPal transactions
└─ UPIProcessor
   └─ Process UPI transactions

Abstract Base:
AbstractPaymentProcessor
├─ validate()
├─ execute()
├─ updateStatus()
└─ generateResponse()
```

### PaymentObserver Interface (Observer)
```
Implementations:
├─ CustomerNotifier
│  └─ Notify customer of transaction
└─ MerchantNotifier
   └─ Notify merchant of transaction
```

---

## 📊 Payment Methods

### Supported Methods
```
PaymentMethod Enum:
├─ CREDIT_CARD
├─ PAYPAL
├─ UPI
└─ (Extensible for more methods)
```

### Payment Status
```
PaymentStatus Enum:
├─ SUCCESS
├─ FAILED
├─ PENDING
└─ (Additional statuses as needed)
```

---

## 🔄 Transaction Lifecycle

```
Request Received
    ↓
Validate Request
    ├─ Amount > 0?
    ├─ Valid currency?
    ├─ Valid payment method?
    └─ Payment details complete?
    ↓
Create PaymentRequest
    ↓
Route to Processor
    ├─ Use Factory to select processor
    └─ Based on PaymentMethod
    ↓
Execute Payment
    ├─ Validate funds/account
    ├─ Process transaction
    ├─ Update transaction status
    └─ Generate response
    ↓
Notify Observers
    ├─ Notify MerchantNotifier
    ├─ Notify CustomerNotifier
    └─ Create Transaction record
    ↓
Return Response
    └─ Include status and details
```

---

## 🔗 Pattern Integration

```
PaymentGatewayService (Singleton)
    ├─ Uses Factory to create processors
    │   └─ PaymentProcessorFactory
    │
    ├─ Uses Strategy for processing
    │   ├─ CreditCardProcessor
    │   ├─ PayPalProcessor
    │   └─ UPIProcessor
    │
    └─ Uses Observer for notifications
        ├─ CustomerNotifier
        └─ MerchantNotifier
```

---

## 💡 Key Features

✓ **Multi-Gateway Support**
- Credit Card processing
- PayPal integration
- UPI payment
- Easily extensible

✓ **Transaction Notifications**
- Customer notifications
- Merchant notifications
- Real-time updates

✓ **Payment Validation**
- Amount validation
- Payment method validation
- Currency validation
- Payment details validation

✓ **Error Handling**
- Failed payment handling
- Invalid payment method
- Insufficient funds
- Transaction timeout

---

## 📋 Scenario Flows

### Scenario 1: Successful Credit Card Payment
```
1. Create PaymentRequest
   - Amount: 150.75
   - Method: CREDIT_CARD
   - CardNumber: 1234...

2. Route to CreditCardProcessor
   - Validate card details
   - Check funds
   - Process transaction

3. Transaction Success
   - Status: SUCCESS
   - TransactionId: TXN123

4. Notify Observers
   - CustomerNotifier: "Payment successful"
   - MerchantNotifier: "Payment received"
```

### Scenario 2: Failed PayPal Payment
```
1. Create PaymentRequest
   - Amount: 500.00
   - Method: PAYPAL
   - PayPalId: user@email.com

2. Route to PayPalProcessor
   - Validate account
   - Check funds
   - Process fails (insufficient)

3. Transaction Failed
   - Status: FAILED
   - Message: "Insufficient funds"

4. Notify Observers
   - CustomerNotifier: "Payment failed"
   - MerchantNotifier: "Payment failed"
```

### Scenario 3: UPI Payment
```
1. Create PaymentRequest
   - Amount: 99.99
   - Method: UPI
   - UPI: user@upi

2. Route to UPIProcessor
   - Validate UPI account
   - Process transaction
   - Confirm OTP (simulated)

3. Transaction Pending/Success
   - Status: SUCCESS
   - TransactionId: UPI456

4. Notify Observers
   - Both stakeholders notified
```

---

## 🚀 Execution Flow

```
START
  │
  ├─► Initialize PaymentGateway (Singleton)
  │   └─► Initialize Factory
  │
  ├─► Register Observers
  │   ├─ Add CustomerNotifier
  │   └─ Add MerchantNotifier
  │
  ├─► Scenario 1: Credit Card (Success)
  │   ├─ Create request
  │   ├─ Process payment
  │   ├─ Notify observers
  │   └─ Display response
  │
  ├─► Scenario 2: PayPal (Failure)
  │   ├─ Create request
  │   ├─ Process payment (fails)
  │   ├─ Notify observers
  │   └─ Display error
  │
  ├─► Scenario 3: UPI (Success)
  │   ├─ Create request
  │   ├─ Process payment
  │   ├─ Notify observers
  │   └─ Display response
  │
  └─► END (All scenarios complete)
```

---

## 📈 Complexity Analysis

| Operation | Complexity | Performance |
|-----------|-----------|-------------|
| Create Request | O(1) | Instant |
| Route to Processor | O(1) | Instant |
| Validate Payment | O(1) | ~100ms |
| Process Transaction | O(1) | ~500ms |
| Notify Observers | O(n) | ~50ms per observer |
| Generate Response | O(1) | Instant |

---

## 🎓 Learning Outcomes

From this project, you'll understand:

1. **Singleton Pattern**: Managing shared payment gateway
2. **Factory Pattern**: Processor creation and selection
3. **Strategy Pattern**: Different payment processing strategies
4. **Observer Pattern**: Multi-observer notifications
5. **Builder Pattern**: Complex object construction
6. **Real-world Constraints**: Payment validation, error handling

---

## 🔍 Extension Points

1. **Add new payment methods** → Create new Processor
2. **Add payment retry logic** → Extend AbstractPaymentProcessor
3. **Add transaction logging** → Implement new Observer
4. **Add encryption** → Add to request/response
5. **Add audit trail** → New Observer implementation

---

*PaymentSystem LLD Architecture Document - Complete Reference*
*Last Updated: June 17, 2026*

