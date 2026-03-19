package processors;

import enums.PaymentStatus;
import models.PaymentRequest;
import models.PaymentResponse;

public class PayPalProcessor extends AbstractPaymentProcessor {
    @Override
    protected PaymentResponse doProcess(PaymentRequest request) {
        System.out.println("Redirecting to PayPal for transaction " + request.getTransactionId());
        // Simulate PayPal API interaction
        return new PaymentResponse(PaymentStatus.SUCCESSFUL, "Paypal payment successful.");
    }
}
