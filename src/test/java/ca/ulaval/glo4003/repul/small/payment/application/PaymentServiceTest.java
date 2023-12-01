package ca.ulaval.glo4003.repul.small.payment.application;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.payment.application.PaymentService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PaymentServiceTest {
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
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
            new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEALKIT_TYPE, A_DELIVERY_LOCATION_ID, A_DELIVERY_DATE);

        assertDoesNotThrow(() -> paymentService.handleMealKitConfirmedEvent(mealKitConfirmedEvent));
    }
}
