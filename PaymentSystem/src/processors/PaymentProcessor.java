package processors;

import models.PaymentRequest;
import models.PaymentResponse;

public interface PaymentProcessor {
    PaymentResponse processPayment(PaymentRequest request);
}
