package ca.ulaval.glo4003.repul.medium.notification.infrastructure;

import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.AccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.infrastructure.EmailNotificationSender;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryAccountRepository;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitDeliveryInfoDto;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceWithEmailManualTest {
    private static final UniqueIdentifier A_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier ANOTHER_VALID_DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_INVALID_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final Email AN_EMAIL = new Email("alexandre.mathieu.7@ulaval.ca");
    private static final Account A_DELIVERY_ACCOUNT = new Account(A_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final Account ANOTHER_DELIVERY_ACCOUNT = new Account(ANOTHER_VALID_DELIVERY_ACCOUNT_ID, AN_EMAIL);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("DESJARDINS");
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("POULIOT");
    private static final LockerId A_LOCKER_ID = new LockerId("A_LOCKER_ID", 1);
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final DeliveryLocationId ANOTHER_DELIVERY_LOCATION_ID = new DeliveryLocationId("VACHON");
    private static final LockerId ANOTHER_LOCKER_ID = new LockerId("ANOTHER_LOCKER_ID", 2);
    private static final UniqueIdentifier ANOTHER_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory().generate();

    private static final List<UniqueIdentifier> AVAILABLE_SHIPPERS_IDS = List.of(A_VALID_DELIVERY_ACCOUNT_ID, ANOTHER_VALID_DELIVERY_ACCOUNT_ID);
    private static final List<MealKitDeliveryInfoDto> MEAL_KIT_DELIVERY_INFO_DTOS =
        List.of(new MealKitDeliveryInfoDto(A_DELIVERY_LOCATION_ID, A_LOCKER_ID, A_MEAL_KIT_ID),
            new MealKitDeliveryInfoDto(ANOTHER_DELIVERY_LOCATION_ID, ANOTHER_LOCKER_ID, ANOTHER_MEAL_KIT_ID));
    private static final MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent =
        new MealKitReceivedForDeliveryEvent(A_CARGO_ID, A_KITCHEN_LOCATION_ID, AVAILABLE_SHIPPERS_IDS, MEAL_KIT_DELIVERY_INFO_DTOS);

    public static void main(String[] args) {
        NotificationSender notificationSender = new EmailNotificationSender();
        AccountRepository accountRepository = new InMemoryAccountRepository();
        NotificationService notificationService = new NotificationService(accountRepository, notificationSender);

        accountRepository.saveOrUpdate(A_DELIVERY_ACCOUNT);
        accountRepository.saveOrUpdate(ANOTHER_DELIVERY_ACCOUNT);

        notificationService.handleMealKitReceivedForDeliveryEvent(mealKitReceivedForDeliveryEvent);
    }
}
