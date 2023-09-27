package ca.ulaval.glo4003.repul.small.infrastructure;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.catalog.Amount;
import ca.ulaval.glo4003.repul.infrastructure.EmulatedPaymentHandler;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmulatedPaymentHandlerTest {
    private static final Amount AN_AMOUNT = new Amount(75.0);

    @Test
    public void whenMakingTransaction_shouldReturnTrue() {
        EmulatedPaymentHandler emulatedPaymentHandler = new EmulatedPaymentHandler();

        assertTrue(emulatedPaymentHandler.makeTransaction(AN_AMOUNT));
    }
}
