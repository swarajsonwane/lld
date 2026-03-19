package processors;

import enums.PaymentStatus;
import models.PaymentRequest;
import models.PaymentResponse;

public class CreditCardProcessor extends AbstractPaymentProcessor {
    @Override
    protected PaymentResponse doProcess(PaymentRequest request) {
        System.out.println("Processing credit card payment of amount " + request.getAmount() + " " + request.getCurrency());
        // Simulate interaction with Visa/Mastercard network
        return new PaymentResponse(PaymentStatus.SUCCESSFUL, "Credit Card payment successful.");
    }
}
