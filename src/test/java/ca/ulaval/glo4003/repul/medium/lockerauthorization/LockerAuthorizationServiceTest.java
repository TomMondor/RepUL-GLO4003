package ca.ulaval.glo4003.repul.medium.lockerauthorization;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.NoCardLinkedToUserException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.infrastructure.InMemoryLockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCardAddedEvent;

import com.google.common.eventbus.Subscribe;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LockerAuthorizationServiceTest {
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final ca.ulaval.glo4003.repul.delivery.domain.LockerId A_DELIVERY_LOCKER_ID =
        new ca.ulaval.glo4003.repul.delivery.domain.LockerId("locker", 55);
    private static final LockerId A_LOCKER_AUTHORIZATION_LOCKER_ID = new LockerId("locker");
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Desjardins");
    private static final LocalDate A_DATE = LocalDate.now();
    private static final LocalTime A_TIME = LocalTime.now();
    private static final UserCardNumber A_USER_CARD_NUMBER = new UserCardNumber("111222333");
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT =
        new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, A_MEAL_KIT_TYPE, A_DELIVERY_LOCATION_ID, A_DATE);
    private static final ConfirmedDeliveryEvent A_CONFIRMED_DELIVERY_EVENT =
        new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, Optional.of(A_DELIVERY_LOCKER_ID), A_TIME);
    private static final UserCardAddedEvent A_USER_CARD_ADDED_EVENT = new UserCardAddedEvent(AN_ACCOUNT_ID, A_USER_CARD_NUMBER);
    private static final OpenLockerQuery A_OPEN_LOCKER_QUERY = new OpenLockerQuery(A_USER_CARD_NUMBER, A_LOCKER_AUTHORIZATION_LOCKER_ID);

    private RepULEventBus eventBus;
    private LockerAuthorizationSystem lockerAuthorizationSystem;
    private LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository;
    private LockerAuthorizationService lockerAuthorizationService;

    @BeforeEach
    public void setUpLockerAuthorizationService() {
        eventBus = new GuavaEventBus();
        lockerAuthorizationSystemRepository = new InMemoryLockerAuthorizationSystemRepository();
        lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
        lockerAuthorizationService = new LockerAuthorizationService(eventBus, lockerAuthorizationSystemRepository);
        eventBus.register(lockerAuthorizationService);
    }

    @Test
    public void givenNoRegisteredCard_whenOpeningLocker_shouldThrowNoCardLinkedToUserException() {
        eventBus.publish(A_MEAL_KIT_CONFIRMED_EVENT);
        eventBus.publish(A_CONFIRMED_DELIVERY_EVENT);

        assertThrows(NoCardLinkedToUserException.class, () -> lockerAuthorizationService.openLocker(A_OPEN_LOCKER_QUERY));
    }

    @Test
    public void givenNoMealKitConfirmed_whenHandlingConfirmedDeliveryEvent_shouldThrowOrderNotFoundException() {
        eventBus.publish(A_USER_CARD_ADDED_EVENT);

        assertThrows(OrderNotFoundException.class, () -> eventBus.publish(A_CONFIRMED_DELIVERY_EVENT));
    }

    @Test
    public void givenAMealKitNotDelivered_whenOpeningLocker_shouldThrowNoCardLinkedToUserException() {
        eventBus.publish(A_MEAL_KIT_CONFIRMED_EVENT);
        eventBus.publish(A_USER_CARD_ADDED_EVENT);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationService.openLocker(A_OPEN_LOCKER_QUERY));
    }

    @Test
    public void givenAllNeededEvents_whenOpenLocker_shouldPublishMealKitPickedUpByUserEvent() {
        eventBus.publish(A_MEAL_KIT_CONFIRMED_EVENT);
        eventBus.publish(A_CONFIRMED_DELIVERY_EVENT);
        eventBus.publish(A_USER_CARD_ADDED_EVENT);
        MealKitPickedUpByUserEventListener mealKitPickedUpByUserEventListener = new MealKitPickedUpByUserEventListener();
        eventBus.register(mealKitPickedUpByUserEventListener);

        lockerAuthorizationService.openLocker(A_OPEN_LOCKER_QUERY);

        assertTrue(mealKitPickedUpByUserEventListener.hasBeenCalled);
    }

    public class MealKitPickedUpByUserEventListener {
        public boolean hasBeenCalled;

        @Subscribe
        public void handleMealKitPickedUpByUserEvent(MealKitPickedUpByUserEvent mealKitPickedUpByUserEvent) {
            hasBeenCalled = true;
        }
    }
}
