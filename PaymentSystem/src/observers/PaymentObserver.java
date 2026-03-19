package observers;

import models.Transaction;

public interface PaymentObserver {
    void onTransactionUpdate(Transaction transaction);
}
