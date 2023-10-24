package ca.ulaval.glo4003.repul.small.notification.service;

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
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.application.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.AccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitDeliveryInfoDto;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;
import ca.ulaval.glo4003.repul.user.application.event.AccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    private static final UniqueIdentifier A_VALID_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier ANOTHER_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_INVALID_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final Email AN_EMAIL = new Email("alexandre.mathieu.7@ulaval.ca");
    private static final Account A_VALID_ACCOUNT = new Account(A_VALID_ACCOUNT_ID, AN_EMAIL);
    private static final Account A_DELIVERY_ACCOUNT = new Account(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final Account ANOTHER_DELIVERY_ACCOUNT = new Account(ANOTHER_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("A_LOCATION_ID");
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("A_DELIVERY_LOCATION_ID");
    private static final LockerId A_LOCKER_ID = new LockerId("A_LOCKER_ID", 1);
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final DeliveryLocationId ANOTHER_DELIVERY_LOCATION_ID = new DeliveryLocationId("ANOTHER_DELIVERY_LOCATION_ID");
    private static final LockerId ANOTHER_LOCKER_ID = new LockerId("ANOTHER_LOCKER_ID", 2);
    private static final UniqueIdentifier ANOTHER_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_TICKET_ID = new UniqueIdentifierFactory().generate();

    private static final List<UniqueIdentifier> AVAILABLE_SHIPPERS_IDS = List.of(A_VALID_DELIVERY_ACCOUNT_ID, ANOTHER_VALID_DELIVERY_ACCOUNT_ID);
    private static final List<UniqueIdentifier> AVAILABLE_SHIPPERS_IDS_WITH_INVALID_ACCOUNT = List.of(A_VALID_DELIVERY_ACCOUNT_ID, AN_INVALID_ACCOUNT_ID);
    private static final List<MealKitDeliveryInfoDto> MEAL_KIT_DELIVERY_INFO_DTOS =
        List.of(new MealKitDeliveryInfoDto(A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_MEAL_KIT_ID),
            new MealKitDeliveryInfoDto(ANOTHER_DELIVERY_LOCATION_ID, ANOTHER_LOCKER_ID, ANOTHER_MEAL_KIT_ID));
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
        new MealKitReceivedForDeliveryEvent(A_TICKET_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_SHIPPERS_IDS, MEAL_KIT_DELIVERY_INFO_DTOS);
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEventWithInvalidDeliveryAccount =
        new MealKitReceivedForDeliveryEvent(A_TICKET_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_SHIPPERS_IDS_WITH_INVALID_ACCOUNT, MEAL_KIT_DELIVERY_INFO_DTOS);

    private NotificationService notificationService;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private NotificationSender notificationSender;

    @BeforeEach
    public void createService() {
        this.notificationService = new NotificationService(accountRepository, notificationSender);
    }

    @Test
    public void whenHandlingAccountCreation_shouldAddItToRepo() {
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent(A_VALID_ACCOUNT_ID, AN_EMAIL);

        this.notificationService.handleAccountCreated(accountCreatedEvent);

        verify(accountRepository, times(1)).saveOrUpdate(A_VALID_ACCOUNT);
    }

    @Test
    public void whenHandlingDeliveryAccountCreationEvent_shouldAddItToRepo() {
        DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent = new DeliveryPersonAccountCreatedEvent(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);

        this.notificationService.handleDeliveryAccountCreated(deliveryPersonAccountCreatedEvent);

        verify(accountRepository, times(1)).saveOrUpdate(A_DELIVERY_ACCOUNT);
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldCallNotificationSenderGoodAmountOfTimes() {
        when(accountRepository.getByAccountId(any())).thenReturn(Optional.of(A_DELIVERY_ACCOUNT));

        this.notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);

        verify(notificationSender, times(AVAILABLE_SHIPPERS_IDS.size())).send(any(Account.class), anyString());
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldCallNotificationSenderWithGoodAccount() {
        when(accountRepository.getByAccountId(A_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(Optional.of(A_DELIVERY_ACCOUNT));
        when(accountRepository.getByAccountId(ANOTHER_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(Optional.of(ANOTHER_DELIVERY_ACCOUNT));

        this.notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);

        String message = "Your meal kits (ticket:" + A_TICKET_ID.value().toString() + ") are ready to be fetched from " + A_KITCHEN_LOCATION_ID.value() + "\n";
        message += "Here is the list of meal kits to be delivered:\n";
        message += "MealKit ID " + A_MEAL_KIT_ID.value() + " to " + A_DELIVERY_LOCATION_ID.value() + " in box " + A_LOCKER_ID.lockerNumber() + "\n";
        message +=
            "MealKit ID " + ANOTHER_MEAL_KIT_ID.value() + " to " + ANOTHER_DELIVERY_LOCATION_ID.value() + " in box " + ANOTHER_LOCKER_ID.lockerNumber() + "\n";

        verify(notificationSender, times(1)).send(A_DELIVERY_ACCOUNT, message);
        verify(notificationSender, times(1)).send(ANOTHER_DELIVERY_ACCOUNT, message);
    }

    @Test
    public void givenAnInvalidAccount_whenHandlingMealKitReceivedForDeliveryEvent_shouldThrowAccountNotFoundException() {
        when(accountRepository.getByAccountId(A_VALID_DELIVERY_ACCOUNT_ID)).thenReturn(Optional.of(A_DELIVERY_ACCOUNT));
        when(accountRepository.getByAccountId(AN_INVALID_ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
            () -> this.notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEventWithInvalidDeliveryAccount));
    }
}
