package ca.ulaval.glo4003.repul.small.lockerauthorization.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LockerAuthorizationServiceTest {
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final LockerId A_LOCKER_CONTEXT_LOCKER_ID = new LockerId("VACHON 1");
    private static final UserCardNumber A_USER_CARD_NUMBER = new UserCardNumber("123123123");
    private static final OpenLockerQuery OPEN_LOCKER_QUERY = new OpenLockerQuery(A_USER_CARD_NUMBER, A_LOCKER_CONTEXT_LOCKER_ID);

    @Mock
    private LockerAuthorizationSystem lockerAuthorizationSystem;
    @Mock
    private LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository;
    @Mock
    private RepULEventBus eventBus;

    private LockerAuthorizationService lockerAuthorizationService;

    @BeforeEach
    public void createLockerAuthorizationService() {
        lockerAuthorizationService = new LockerAuthorizationService(eventBus, lockerAuthorizationSystemRepository);

        when(lockerAuthorizationSystemRepository.get()).thenReturn(lockerAuthorizationSystem);
    }

    @Test
    public void whenCreatingOrder_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.createOrder(A_SUBSCRIBER_ID, A_MEAL_KIT_ID);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenCreatingOrder_shouldCreateOrder() {
        lockerAuthorizationService.createOrder(A_SUBSCRIBER_ID, A_MEAL_KIT_ID);

        verify(lockerAuthorizationSystem).createOrder(A_SUBSCRIBER_ID, A_MEAL_KIT_ID);
    }

    @Test
    public void whenCreatingOrder_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.createOrder(A_SUBSCRIBER_ID, A_MEAL_KIT_ID);

        verify(lockerAuthorizationSystemRepository).save(lockerAuthorizationSystem);
    }

    @Test
    public void whenAssigningLockerToMealKit_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.assignLockerToMealKit(A_MEAL_KIT_ID, A_LOCKER_CONTEXT_LOCKER_ID);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenAssigningLockerToMealKit_shouldAssignLocker() {
        lockerAuthorizationService.assignLockerToMealKit(A_MEAL_KIT_ID, A_LOCKER_CONTEXT_LOCKER_ID);

        verify(lockerAuthorizationSystem).assignLocker(A_MEAL_KIT_ID, A_LOCKER_CONTEXT_LOCKER_ID);
    }

    @Test
    public void whenAssigningLockerToMealKit_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.assignLockerToMealKit(A_MEAL_KIT_ID, A_LOCKER_CONTEXT_LOCKER_ID);

        verify(lockerAuthorizationSystemRepository).save(lockerAuthorizationSystem);
    }

    @Test
    public void whenRecallingMealKit_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.unassignLocker(A_MEAL_KIT_ID);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenRecallingMealKit_shouldUnassignLocker() {
        lockerAuthorizationService.unassignLocker(A_MEAL_KIT_ID);

        verify(lockerAuthorizationSystem).unassignLocker(A_MEAL_KIT_ID);
    }

    @Test
    public void whenRecallingMealKit_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.unassignLocker(A_MEAL_KIT_ID);

        verify(lockerAuthorizationSystemRepository).save(lockerAuthorizationSystem);
    }

    @Test
    public void whenRegisteringUserCardNumber_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.registerSubscriberCardNumber(A_SUBSCRIBER_ID, A_USER_CARD_NUMBER);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenRegisteringUserCardNumber_shouldRegisterUserCardNumber() {
        lockerAuthorizationService.registerSubscriberCardNumber(A_SUBSCRIBER_ID, A_USER_CARD_NUMBER);

        verify(lockerAuthorizationSystem).registerSubscriberCardNumber(A_SUBSCRIBER_ID, A_USER_CARD_NUMBER);
    }

    @Test
    public void whenRegisteringUserCardNumber_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.registerSubscriberCardNumber(A_SUBSCRIBER_ID, A_USER_CARD_NUMBER);

        verify(lockerAuthorizationSystemRepository).save(lockerAuthorizationSystem);
    }

    @Test
    public void whenOpenLocker_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.openLocker(OPEN_LOCKER_QUERY);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenOpenLocker_shouldAuthorizeWithLockerAuthorizationSystem() {
        lockerAuthorizationService.openLocker(OPEN_LOCKER_QUERY);

        verify(lockerAuthorizationSystem).authorize(OPEN_LOCKER_QUERY.lockerId(), OPEN_LOCKER_QUERY.userCardNumber());
    }

    @Test
    public void whenOpenLocker_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.openLocker(OPEN_LOCKER_QUERY);

        verify(lockerAuthorizationSystemRepository).save(lockerAuthorizationSystem);
    }

    @Test
    public void whenOpenLocker_shouldPublishMealKitPickedUpByUserEvent() {
        lockerAuthorizationService.openLocker(OPEN_LOCKER_QUERY);

        verify(eventBus).publish(any(MealKitPickedUpByUserEvent.class));
    }
}
