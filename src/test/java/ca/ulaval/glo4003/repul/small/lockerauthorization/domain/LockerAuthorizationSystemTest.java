package ca.ulaval.glo4003.repul.small.lockerauthorization.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.NoCardLinkedToUserException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.UserCardNotAuthorizedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LockerAuthorizationSystemTest {
    private static final UniqueIdentifier AN_USER_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UserCardNumber A_USER_CARD_NUMBER = new UserCardNumber("123456789");
    private static final UserCardNumber ANOTHER_USER_CARD_NUMBER = new UserCardNumber("987654321");
    private static final LockerId A_LOCKER_ID = new LockerId("1234");

    @Test
    public void givenUserWithNoCardLinked_whenAuthorize_shouldThrowNoCardLinkedToUserException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);

        assertThrows(NoCardLinkedToUserException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenLockerIdNotLinkedToOrder_whenAuthorize_shouldThrowLockerNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenLockerIdThatHasBeenUnassignedFromOrder_whenAuthorize_shouldThrowLockerNotFoundException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.unassignLocker(A_MEAL_KIT_ID);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenNotAuthorizedUserCardNumber_whenAuthorize_shouldThrowUserCardNotAuthorizedException() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerUserCardNumber(AN_USER_ID, A_USER_CARD_NUMBER);

        assertThrows(UserCardNotAuthorizedException.class,
            () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, ANOTHER_USER_CARD_NUMBER));
    }

    @Test
    public void givenAuthorizedUserCardNumberAndCompleteInformation_whenAuthorize_shouldNotThrow() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerUserCardNumber(AN_USER_ID, A_USER_CARD_NUMBER);

        assertDoesNotThrow(() -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }

    @Test
    public void givenAuthorizedUserCardNumber_whenAuthorize_shouldRemoveOrderFromList() {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        lockerAuthorizationSystem.createOrder(AN_USER_ID, A_MEAL_KIT_ID);
        lockerAuthorizationSystem.assignLocker(A_MEAL_KIT_ID, A_LOCKER_ID);
        lockerAuthorizationSystem.registerUserCardNumber(AN_USER_ID, A_USER_CARD_NUMBER);

        lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER);

        assertThrows(LockerNotAssignedException.class, () -> lockerAuthorizationSystem.authorize(A_LOCKER_ID, A_USER_CARD_NUMBER));
    }
}
