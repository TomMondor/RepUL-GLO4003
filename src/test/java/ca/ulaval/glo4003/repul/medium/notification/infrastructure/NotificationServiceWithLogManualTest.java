package ca.ulaval.glo4003.repul.medium.notification.infrastructure;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.AccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.LogNotificationSender;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceWithLogManualTest {
    private static final UniqueIdentifier A_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier ANOTHER_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_INVALID_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final Email AN_EMAIL = new Email("alexandre.mathieu.7@ulaval.ca");
    private static final Account A_DELIVERY_ACCOUNT = new Account(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final Account ANOTHER_DELIVERY_ACCOUNT = new Account(ANOTHER_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("DESJARDINS");
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("POULIOT");
    private static final Optional<LockerId> A_OPTINAL_LOCKER_ID = Optional.of(new LockerId("A_LOCKER_ID", 1));
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final DeliveryLocationId ANOTHER_DELIVERY_LOCATION_ID = new DeliveryLocationId("VACHON");
    private static final Optional<LockerId> ANOTHER_LOCKER_ID = Optional.of(new LockerId("ANOTHER_LOCKER_ID", 2));
    private static final UniqueIdentifier ANOTHER_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory().generate();

    private static final List<UniqueIdentifier> AVAILABLE_SHIPPERS_IDS = List.of(A_VALID_DELIVERY_ACCOUNT_ID, ANOTHER_VALID_DELIVERY_ACCOUNT_ID);
    private static final List<MealKitDto> MEAL_KIT_DTOS =
        List.of(new MealKitDto(A_DELIVERY_LOCATION_ID, A_OPTINAL_LOCKER_ID, A_MEAL_KIT_ID),
            new MealKitDto(ANOTHER_DELIVERY_LOCATION_ID, ANOTHER_LOCKER_ID, ANOTHER_MEAL_KIT_ID));
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
        new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_SHIPPERS_IDS, MEAL_KIT_DTOS);

    public static void main(String[] args) {
        NotificationSender notificationSender = new LogNotificationSender();
        AccountRepository accountRepository = new InMemoryAccountRepository();
        NotificationService notificationService = new NotificationService(accountRepository, notificationSender);

        accountRepository.saveOrUpdate(A_DELIVERY_ACCOUNT);
        accountRepository.saveOrUpdate(ANOTHER_DELIVERY_ACCOUNT);

        notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);
    }
}
