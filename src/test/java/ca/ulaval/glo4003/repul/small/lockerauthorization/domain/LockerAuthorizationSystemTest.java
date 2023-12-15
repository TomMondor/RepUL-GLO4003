package ca.ulaval.glo4003.repul.small.lockerauthorization.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.NoCardLinkedToUserException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.SubscriberCardNotAuthorizedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LockerAuthorizationSystemTest {
    private static final SubscriberUniqueIdentifier AN_USER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberCardNumber A_SUBSCRIBER_CARD_NUMBER = new SubscriberCardNumber("123456789");
    private static final SubscriberCardNumber ANOTHER_SUBSCRIBER_CARD_NUMBER = new SubscriberCardNumber("987654321");
    private static final LockerId A_LOCKER_ID = new LockerId("1234");

    @Test
    public void givenUserWithNoCardLinked_whenAuthorize_shouldThrowNoCardLinkedToUserException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);

        assertThrows(NoCardLinkedToUserException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID,
            A_SUBSCRIBER_CARD_NUMBER));
    }

    @Test
    public void givenLockerIdNotLinkedToOrder_whenAuthorize_shouldThrowLockerNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID,
            A_SUBSCRIBER_CARD_NUMBER));
    }

    @Test
    public void givenLockerIdThatHasBeenUnassignedFromOrder_whenAuthorize_shouldThrowLockerNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.unassignLocker(A_MEAL_KIT_ID);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID,
            A_SUBSCRIBER_CARD_NUMBER));
    }

    @Test
    public void givenNotAuthorizedSubscriberCardNumber_whenAuthorize_shouldThrowSubscriberCardNotAuthorizedException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerSubscriberCardNumber(AN_USER_ID, A_SUBSCRIBER_CARD_NUMBER);

        assertThrows(SubscriberCardNotAuthorizedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID,
            ANOTHER_SUBSCRIBER_CARD_NUMBER));
    }

    @Test
    public void givenAuthorizedSubscriberCardNumberAndCompleteInformation_whenAuthorize_shouldNotThrow() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerSubscriberCardNumber(AN_USER_ID, A_SUBSCRIBER_CARD_NUMBER);

        assertDoesNotThrow(() -> lockerAuthorizationSystem.authorize(A_LOCKER_ID,
            A_SUBSCRIBER_CARD_NUMBER));
    }

    @Test
    public void givenAuthorizedSubscriberCardNumber_whenAuthorize_shouldRemoveOrderFromList() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerSubscriberCardNumber(AN_USER_ID, A_SUBSCRIBER_CARD_NUMBER);

        lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_SUBSCRIBER_CARD_NUMBER);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID,
            A_SUBSCRIBER_CARD_NUMBER));
    }

    @Test
    public void givenNoOrder_whenAssigningLocker_shouldThrowOrderNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();

        assertThrows(OrderNotFoundException.class, () -> lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID));
    }

    @Test
    public void givenNoOrder_whenUnassigningLocker_shouldThrowOrderNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();

        assertThrows(OrderNotFoundException.class, () -> lockerAuthorizationSystem.unassignLocker(A_MEAL_KIT_ID));
    }
}
