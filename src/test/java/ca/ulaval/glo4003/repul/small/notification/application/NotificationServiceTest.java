package ca.ulaval.glo4003.repul.small.notification.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.application.exception.DeliveryPersonAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.application.exception.UserAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.AccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    private static final UniqueIdentifier A_VALID_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_VALID_SUBSCRIPTION_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier ANOTHER_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_INVALID_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final Email AN_EMAIL = new Email("alexandre.mathieu.7@ulaval.ca");
    private static final DeliveryPersonAccount A_DELIVERY_ACCOUNT = new DeliveryPersonAccount(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final DeliveryPersonAccount ANOTHER_DELIVERY_ACCOUNT = new DeliveryPersonAccount(ANOTHER_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final UserAccount
        A_VALID_USER_ACCOUNT = new UserAccount(A_VALID_ACCOUNT_ID, AN_EMAIL);
    private static final LocalTime A_TIME = LocalTime.now();
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("A_LOCATION_ID");
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("A_DELIVERY_LOCATION_ID");
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("A_LOCKER_ID", 1));
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final DeliveryLocationId ANOTHER_DELIVERY_LOCATION_ID = new DeliveryLocationId("ANOTHER_DELIVERY_LOCATION_ID");
    private static final Optional<LockerId> ANOTHER_LOCKER_ID = Optional.of(new LockerId("ANOTHER_LOCKER_ID", 2));
    private static final UniqueIdentifier ANOTHER_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory().generate();
    private static final List<UniqueIdentifier> AVAILABLE_DELIVERY_PEOPLE_IDS = List.of(A_VALID_DELIVERY_ACCOUNT_ID, ANOTHER_VALID_DELIVERY_ACCOUNT_ID);
    private static final List<UniqueIdentifier> AVAILABLE_DELIVERY_PEOPLE_IDS_WITH_INVALID_ACCOUNT =
        List.of(A_VALID_DELIVERY_ACCOUNT_ID, AN_INVALID_ACCOUNT_ID);
    private static final List<MealKitDto> MEAL_KIT_DTOS = List.of(new MealKitDto(A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_MEAL_KIT_ID),
        new MealKitDto(ANOTHER_DELIVERY_LOCATION_ID, ANOTHER_LOCKER_ID, ANOTHER_MEAL_KIT_ID));
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
        new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_DELIVERY_PEOPLE_IDS, MEAL_KIT_DTOS);
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEventWithInvalidDeliveryAccount =
        new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_DELIVERY_PEOPLE_IDS_WITH_INVALID_ACCOUNT, MEAL_KIT_DTOS);
    private static final ConfirmedDeliveryEvent MEAL_KIT_DELIVERED_EVENT =
        new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_TIME);
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT =
        new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_VALID_SUBSCRIPTION_ID, A_VALID_ACCOUNT_ID,
            MealKitType.STANDARD, A_DELIVERY_LOCATION_ID, LocalDate.now());
    private static final AccountCreatedEvent AN_ACCOUNT_CREATED_EVENT = new AccountCreatedEvent(A_VALID_ACCOUNT_ID, AN_EMAIL);
    private static final DeliveryPersonAccountCreatedEvent A_DELIVERY_PERSON_ACCOUNT_CREATED_EVENT =
        new DeliveryPersonAccountCreatedEvent(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);

    private NotificationService notificationService;

    @Mock
    private DeliveryPersonAccountRepository deliveryPersonAccountRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private NotificationSender notificationSender;

    @BeforeEach
    public void createService() {
        this.notificationService = new NotificationService(userAccountRepository,
            deliveryPersonAccountRepository, notificationSender);
    }

    @Test
    public void whenHandlingUserAccountCreation_shouldSaveOrUpdateUserAccountRepository() {
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent(A_VALID_ACCOUNT_ID, AN_EMAIL);

        this.notificationService.handleUserAccountCreated(accountCreatedEvent);

        verify(userAccountRepository, times(1)).saveOrUpdate(any(UserAccount.class));
    }

    @Test
    public void whenHandlingAccountDeliveryCreation_shouldSaveOrUpdateDeliveryAccountRepository() {
        DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent = new DeliveryPersonAccountCreatedEvent(A_VALID_ACCOUNT_ID, AN_EMAIL);

        this.notificationService.handleDeliveryAccountCreated(deliveryPersonAccountCreatedEvent);

        verify(deliveryPersonAccountRepository, times(1)).saveOrUpdate(
            any(DeliveryPersonAccount.class));
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldCallNotificationSenderGoodAmountOfTimes() {
        when(deliveryPersonAccountRepository.getByAccountId(any())).thenReturn(Optional.of(A_DELIVERY_ACCOUNT));

        this.notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);

        verify(notificationSender, times(AVAILABLE_DELIVERY_PEOPLE_IDS.size())).send(any(Account.class), any(NotificationMessage.class));
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldCallNotificationSenderWithRightAccountAndAMessage() {
        when(deliveryPersonAccountRepository.getByAccountId(A_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(Optional.of(A_DELIVERY_ACCOUNT));
        when(deliveryPersonAccountRepository.getByAccountId(ANOTHER_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(Optional.of(ANOTHER_DELIVERY_ACCOUNT));

        this.notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);

        verify(notificationSender, times(1)).send(eq(A_DELIVERY_ACCOUNT), any(NotificationMessage.class));
        verify(notificationSender, times(1)).send(eq(ANOTHER_DELIVERY_ACCOUNT), any(NotificationMessage.class));
    }

    @Test
    public void whenHandlingMealKitDeliveredEvent_shouldCallNotificationSenderWithRightAccountAndAMessage() {
        when(userAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID)).thenReturn(Optional.of(A_VALID_USER_ACCOUNT));

        this.notificationService.handleConfirmedDeliveryEvent(MEAL_KIT_DELIVERED_EVENT);

        verify(notificationSender, times(1)).send(eq(A_VALID_USER_ACCOUNT), any(NotificationMessage.class));
    }

    @Test
    public void givenAnInvalidAccount_whenHandlingMealKitReceivedForDeliveryEvent_shouldThrowDeliveryPersonAccountNotFoundException() {
        when(deliveryPersonAccountRepository.getByAccountId(A_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(Optional.of(A_DELIVERY_ACCOUNT));
        when(deliveryPersonAccountRepository.getByAccountId(AN_INVALID_ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(DeliveryPersonAccountNotFoundException.class,
            () -> this.notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEventWithInvalidDeliveryAccount));
    }

    @Test
    public void whenHandleMealKitDeliveredEvent_shouldCallNotificationSenderWithGoodAccount() {
        when(userAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID)).thenReturn(Optional.of(A_VALID_USER_ACCOUNT));

        this.notificationService.handleConfirmedDeliveryEvent(MEAL_KIT_DELIVERED_EVENT);

        verify(notificationSender, times(1)).send(eq(A_VALID_USER_ACCOUNT), any(NotificationMessage.class));
    }

    @Test
    public void givenMealKitAssociatedWithNoAccount_whenHandleMealKitDeliveredEvent_shouldThrowUserAccountNotFoundException() {
        when(userAccountRepository.getAccountByMealKitId(A_MEAL_KIT_ID)).thenReturn(Optional.empty());

        assertThrows(UserAccountNotFoundException.class,
            () -> this.notificationService.handleConfirmedDeliveryEvent(MEAL_KIT_DELIVERED_EVENT));
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_ShouldAddMealKitToUser() {
        when(userAccountRepository.getAccountById(A_VALID_ACCOUNT_ID)).thenReturn(
            Optional.of(A_VALID_USER_ACCOUNT));
        int numberOfMealKits = A_VALID_USER_ACCOUNT.getMealKitIds().size();

        this.notificationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        assertEquals(A_VALID_USER_ACCOUNT.getMealKitIds().size(), numberOfMealKits + 1);
    }

    @Test
    public void givenNoAccount_whenHandleMealKitConfirmedEvent_shouldThrowUserAccountNotFoundException() {
        when(userAccountRepository.getAccountById(A_VALID_ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(UserAccountNotFoundException.class,
            () -> this.notificationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT));
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldSaveOrUpdateMealKitInUserAccountRepository() {
        when(userAccountRepository.getAccountById(A_VALID_ACCOUNT_ID)).thenReturn(Optional.of(A_VALID_USER_ACCOUNT));

        this.notificationService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(userAccountRepository).saveOrUpdate(any(UserAccount.class));
    }

    @Test
    public void whenHandlingUserAccountCreated_shouldSaveOrUpdateInUserAccountRepository() {
        this.notificationService.handleUserAccountCreated(AN_ACCOUNT_CREATED_EVENT);

        verify(userAccountRepository).saveOrUpdate(any(UserAccount.class));
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreated_shouldSaveOrUpdateInDeliveryPersonAccountRepository() {
        this.notificationService.handleDeliveryAccountCreated(A_DELIVERY_PERSON_ACCOUNT_CREATED_EVENT);

        verify(deliveryPersonAccountRepository).saveOrUpdate(any(DeliveryPersonAccount.class));
    }
}
