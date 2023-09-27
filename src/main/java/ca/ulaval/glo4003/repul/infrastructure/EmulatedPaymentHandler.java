package ca.ulaval.glo4003.repul.infrastructure;

import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;

public class EmulatedPaymentHandler implements PaymentHandler {
    @Override
    public Boolean makeTransaction(Amount amount) {
        return true;
    }
}
