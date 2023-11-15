package ca.ulaval.glo4003.repul.medium.notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.application.exception.DeliveryPersonAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.application.exception.UserAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryDeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryUserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.AccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final Email AN_EMAIL = new Email("idul12@ulaval.ca");
    private static final Email ANOTHER_EMAIL = new Email("idul456@ulaval.ca");
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("DESJARDINS");
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(2);
    private static final LocalTime A_DELIVERY_TIME = LocalTime.now().plusHours(48);
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("LOCKER_ID", 121135));
    private static final UniqueIdentifier A_CARGO_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final KitchenLocationId A_KITCHEN_ID = new KitchenLocationId("KITCHEN_ID");
    private static final UniqueIdentifier A_DELIVERY_PERSON_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_DELIVERY_PERSON_ID = new UniqueIdentifier(UUID.randomUUID());
    private RepULEventBus eventBus;
    @Mock
    private NotificationSender notificationSender;

    @BeforeEach
    public void createService() {
        eventBus = new GuavaEventBus();
        UserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        DeliveryPersonAccountRepository deliveryPersonAccountRepository = new InMemoryDeliveryPersonAccountRepository();
        NotificationService notificationService = new NotificationService(userAccountRepository, deliveryPersonAccountRepository, notificationSender);
        eventBus.register(notificationService);
    }

    @Test
    public void givenNoMatchingUserAccountCreated_whenHandlingMealKitConfirmedEvent_shouldThrowDeliveryPersonAccountNotFoundException() {
        assertThrows(UserAccountNotFoundException.class, () -> eventBus.publish(
            new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, A_MEAL_KIT_TYPE, A_DELIVERY_LOCATION_ID, A_DELIVERY_DATE)));
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldSendNotificationToUser() {
        eventBus.publish(new AccountCreatedEvent(AN_ACCOUNT_ID, AN_EMAIL));
        eventBus.publish(new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, A_MEAL_KIT_TYPE, A_DELIVERY_LOCATION_ID, A_DELIVERY_DATE));

        eventBus.publish(new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_DELIVERY_TIME));

        verify(notificationSender).send(any(Account.class), any(String.class));
    }

    @Test
    public void givenNoMatchingMealKitConfirmed_whenHandlingConfirmedDeliveryEvent_shouldThrowDeliveryPersonAccountNotFoundException() {
        eventBus.publish(new AccountCreatedEvent(AN_ACCOUNT_ID, AN_EMAIL));

        assertThrows(UserAccountNotFoundException.class,
            () -> eventBus.publish(new ConfirmedDeliveryEvent(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_DELIVERY_TIME)));
    }

    @Test
    public void whenHandlingMealKitReceivedForDeliveryEvent_shouldSendNotificationToAllAvailableDeliveryPeople() {
        eventBus.publish(new DeliveryPersonAccountCreatedEvent(A_DELIVERY_PERSON_ID, AN_EMAIL));
        eventBus.publish(new DeliveryPersonAccountCreatedEvent(ANOTHER_DELIVERY_PERSON_ID, ANOTHER_EMAIL));

        MealKitDto mealKitDto = new MealKitDto(A_DELIVERY_LOCATION_ID,  A_LOCKER_ID, A_MEAL_KIT_ID);
        eventBus.publish(new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_ID,
            List.of(A_DELIVERY_PERSON_ID, ANOTHER_DELIVERY_PERSON_ID), List.of(mealKitDto)));

        verify(notificationSender, times(2)).send(any(Account.class), any(String.class));
    }

    @Test
    public void givenNoMatchingDeliveryPersonAccountCreated_whenHandlingMealKitReceivedForDeliveryEvent_shouldThrowDeliveryPersonAccountNotFoundException() {
        MealKitDto mealKitDto = new MealKitDto(A_DELIVERY_LOCATION_ID,  A_LOCKER_ID, A_MEAL_KIT_ID);

        assertThrows(DeliveryPersonAccountNotFoundException.class,
            () -> eventBus.publish(new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_ID, List.of(A_DELIVERY_PERSON_ID), List.of(mealKitDto))));
    }
}
