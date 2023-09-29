package ca.ulaval.glo4003.repul.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.config.DevApplicationContext;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;

public class EmulatedPaymentHandler implements PaymentHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevApplicationContext.class);

    @Override
    public Boolean makeTransaction(Amount amount) {
        LOGGER.info("Le compte a été débité de {}", amount);
        return true;
    }
}
