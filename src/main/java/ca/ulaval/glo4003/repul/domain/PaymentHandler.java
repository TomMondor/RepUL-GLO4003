package ca.ulaval.glo4003.repul.domain;

import ca.ulaval.glo4003.repul.domain.catalog.Amount;

public interface PaymentHandler {
    Boolean makeTransaction(Amount amount);
}
