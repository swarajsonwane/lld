package factory;

import processors.CreditCardProcessor;
import processors.PayPalProcessor;
import processors.PaymentProcessor;
import enums.PaymentMethod;
import processors.UPIProcessor;


public class PaymentProcessorFactory {
    public static PaymentProcessor getProcessor(PaymentMethod method) {
        return switch (method) {
            case CREDIT_CARD -> new CreditCardProcessor();
            case UPI -> new UPIProcessor();
            case PAYPAL -> new PayPalProcessor();
            // case BANK_TRANSFER -> new BankTransferProcessor();
            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        };
    }
}
