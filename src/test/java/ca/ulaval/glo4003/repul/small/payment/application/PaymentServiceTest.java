package ca.ulaval.glo4003.repul.small.payment.application;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.payment.application.PaymentService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PaymentServiceTest {
    private static final UniqueIdentifier A_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory().generate();
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Desjardins");
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now();
    private PaymentService paymentService;

    @BeforeEach
    public void createPaymentService() {
        paymentService = new PaymentService();
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldAcceptAndNotThrow() {
        MealKitConfirmedEvent mealKitConfirmedEvent =
            new MealKitConfirmedEvent(A_UNIQUE_IDENTIFIER, A_UNIQUE_IDENTIFIER, A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, A_DELIVERY_LOCATION_ID, A_DELIVERY_DATE);

        assertDoesNotThrow(() -> paymentService.handleMealKitConfirmedEvent(mealKitConfirmedEvent));
    }
}
