package processors;

import enums.PaymentStatus;
import models.PaymentRequest;
import models.PaymentResponse;

public class UPIProcessor extends AbstractPaymentProcessor {
    @Override
    protected PaymentResponse doProcess(PaymentRequest request) {
        System.out.println("Processing UPI payment of " + request.getAmount() + " " + request.getCurrency());
        return new PaymentResponse(PaymentStatus.SUCCESSFUL, "UPI payment successful.");
    }
}
