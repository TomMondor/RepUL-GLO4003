package ca.ulaval.glo4003.repul.medium.payment;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.payment.application.PaymentService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PaymentServiceTest {
    private static final MealKitUniqueIdentifier A_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_LOCATION_ID = new DeliveryLocationId("VACHON");
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(3);
    private RepULEventBus eventBus;
    private PaymentService paymentService;

    @BeforeEach
    public void createPaymentService() {
        eventBus = new GuavaEventBus();
        paymentService = new PaymentService();
        eventBus.register(paymentService);
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldHandleWithoutThrowing() {
        MealKitConfirmedEvent mealKitConfirmedEvent =
            new MealKitConfirmedEvent(A_MEALKIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, A_MEALKIT_TYPE, A_LOCATION_ID, A_DELIVERY_DATE);

        assertDoesNotThrow(() -> eventBus.publish(mealKitConfirmedEvent));
    }
}
