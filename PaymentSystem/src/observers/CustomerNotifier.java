package observers;

import enums.PaymentStatus;
import models.Transaction;

public class CustomerNotifier implements PaymentObserver {
    @Override
    public void onTransactionUpdate(Transaction transaction) {
        if (transaction.getStatus() == PaymentStatus.SUCCESSFUL) {
            System.out.println("--- CUSTOMER EMAIL ---");
            System.out.println("Your payment of " + transaction.getRequest().getAmount() + " was successful. Transaction ID: " + transaction.getId());
            System.out.println("----------------------");
        }
    }

}

