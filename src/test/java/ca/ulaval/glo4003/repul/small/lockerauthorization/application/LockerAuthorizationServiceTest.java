package ca.ulaval.glo4003.repul.small.lockerauthorization.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCardAddedEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LockerAuthorizationServiceTest {
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId(UUID.randomUUID().toString());
    private static final LocalDate A_DATE = LocalDate.now();
    private static final LocalTime A_TIME = LocalTime.now();
    private static final LockerId A_LOCKER_CONTEXT_LOCKER_ID = new LockerId("VACHON 1");
    private static final ca.ulaval.glo4003.repul.delivery.domain.LockerId A_DELIVERY_LOCKER_ID =
        new ca.ulaval.glo4003.repul.delivery.domain.LockerId("VACHON 1", 1);
    private static final UserCardNumber A_USER_CARD_NUMBER = new UserCardNumber("123123123");
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT = new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID,
        A_MEAL_KIT_TYPE, A_DELIVERY_LOCATION_ID, A_DATE);
    private static final ConfirmedDeliveryEvent A_CONFIRMED_DELIVERY_EVENT = new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID,
        Optional.of(A_DELIVERY_LOCKER_ID), A_TIME);
    private static final RecalledDeliveryEvent A_RECALLED_DELIVERY_EVENT = new RecalledDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCKER_ID,
        A_DELIVERY_LOCATION_ID);
    private static final UserCardAddedEvent A_USER_CARD_ADDED_EVENT = new UserCardAddedEvent(AN_ACCOUNT_ID, A_USER_CARD_NUMBER);
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

        when(lockerAuthorizationSystemRepository.get()).thenReturn(Optional.of(lockerAuthorizationSystem));
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldCreateOrder() {
        lockerAuthorizationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(lockerAuthorizationSystem).createOrder(AN_ACCOUNT_ID, A_MEAL_KIT_ID);
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(lockerAuthorizationSystemRepository).saveOrUpdate(lockerAuthorizationSystem);
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.handleConfirmedDeliveryEvent(A_CONFIRMED_DELIVERY_EVENT);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldAssignLocker() {
        lockerAuthorizationService.handleConfirmedDeliveryEvent(A_CONFIRMED_DELIVERY_EVENT);

        verify(lockerAuthorizationSystem).assignLocker(A_MEAL_KIT_ID, A_LOCKER_CONTEXT_LOCKER_ID);
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.handleConfirmedDeliveryEvent(A_CONFIRMED_DELIVERY_EVENT);

        verify(lockerAuthorizationSystemRepository).saveOrUpdate(lockerAuthorizationSystem);
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.handleRecalledDeliveryEvent(A_RECALLED_DELIVERY_EVENT);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldUnassignLocker() {
        lockerAuthorizationService.handleRecalledDeliveryEvent(A_RECALLED_DELIVERY_EVENT);

        verify(lockerAuthorizationSystem).unassignLocker(A_MEAL_KIT_ID);
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.handleRecalledDeliveryEvent(A_RECALLED_DELIVERY_EVENT);

        verify(lockerAuthorizationSystemRepository).saveOrUpdate(lockerAuthorizationSystem);
    }

    @Test
    public void whenHandlingUserCardAddedEvent_shouldGetLockerAuthorizationSystem() {
        lockerAuthorizationService.handleUserCardAddedEvent(A_USER_CARD_ADDED_EVENT);

        verify(lockerAuthorizationSystemRepository).get();
    }

    @Test
    public void whenHandlingUserCardAddedEvent_shouldRegisterUserCardNumber() {
        lockerAuthorizationService.handleUserCardAddedEvent(A_USER_CARD_ADDED_EVENT);

        verify(lockerAuthorizationSystem).registerUserCardNumber(AN_ACCOUNT_ID, A_USER_CARD_NUMBER);
    }

    @Test
    public void whenHandlingUserCardAddedEvent_shouldSaveLockerAuthorizationSystem() {
        lockerAuthorizationService.handleUserCardAddedEvent(A_USER_CARD_ADDED_EVENT);

        verify(lockerAuthorizationSystemRepository).saveOrUpdate(lockerAuthorizationSystem);
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

        verify(lockerAuthorizationSystemRepository).saveOrUpdate(lockerAuthorizationSystem);
    }

    @Test
    public void whenOpenLocker_shouldPublishMealKitPickedUpByUserEvent() {
        lockerAuthorizationService.openLocker(OPEN_LOCKER_QUERY);

        verify(eventBus).publish(any(MealKitPickedUpByUserEvent.class));
    }
}
