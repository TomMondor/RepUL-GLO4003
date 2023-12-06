package ca.ulaval.glo4003.repul.small.delivery.api;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.api.DeliveryEventHandler;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeliveryEventHandlerTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_UNIQUE_IDENTIFIER =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final Email AN_EMAIL = new Email("unCourriel@ulaval.ca");
    private static final String A_KITCHEN_LOCATION_ID_AS_STRING = "Id";
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId(A_KITCHEN_LOCATION_ID_AS_STRING);
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Vachon");
    private static final LocalDate A_DATE = LocalDate.now();
    private static final DeliveryPersonAccountCreatedEvent A_DELIVERY_PERSON_ACCOUNT_CREATED_EVENT =
        new DeliveryPersonAccountCreatedEvent(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, AN_EMAIL);
    private static final MealKitsCookedEvent A_MEAL_KITS_COOKED_EVENT =
        new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID_AS_STRING, List.of(A_MEAL_KIT_ID));
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT =
        new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID,
            A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE,
            A_DELIVERY_LOCATION_ID, A_DATE);
    private static final RecallCookedMealKitEvent A_RECALL_COOKED_MEAL_KIT_EVENT =
        new RecallCookedMealKitEvent(A_MEAL_KIT_ID);
    private static final MealKitPickedUpByUserEvent A_MEAL_KIT_PICKED_UP_BY_USER_EVENT =
        new MealKitPickedUpByUserEvent(A_MEAL_KIT_ID);

    private DeliveryEventHandler deliveryEventHandler;

    @Mock
    private DeliveryService deliveryService;

    @BeforeEach
    public void createDeliveryEventHandler() {
        deliveryEventHandler = new DeliveryEventHandler(deliveryService);
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldCallCreateDeliveryPersonAccountInService() {
        deliveryEventHandler.handleDeliveryPersonAccountCreatedEvent(A_DELIVERY_PERSON_ACCOUNT_CREATED_EVENT);

        verify(deliveryService, times(1)).createDeliveryPersonAccount(
            A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldCallCreateCargoInService() {
        deliveryEventHandler.handleMealKitsCookedEvent(A_MEAL_KITS_COOKED_EVENT);

        verify(deliveryService, times(1))
            .createCargo(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldCallReceiveMealKitForDeliveryInService() {
        deliveryEventHandler.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(deliveryService, times(1))
            .receiveMealKitForDelivery(A_DELIVERY_LOCATION_ID, A_MEAL_KIT_ID);
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldCallRecallMealKitInService() {
        deliveryEventHandler.handleRecallCookedMealKitEvent(A_RECALL_COOKED_MEAL_KIT_EVENT);

        verify(deliveryService, times(1)).recallMealKit(A_MEAL_KIT_ID);
    }

    @Test
    public void whenHandlingMealKitPickUpByUserEvent_shouldCallRemoveMealKitFromLockerInService() {
        deliveryEventHandler.handleMealKitPickedUpByUserEvent(A_MEAL_KIT_PICKED_UP_BY_USER_EVENT);

        verify(deliveryService, times(1)).removeMealKitFromLocker(A_MEAL_KIT_ID);
    }
}