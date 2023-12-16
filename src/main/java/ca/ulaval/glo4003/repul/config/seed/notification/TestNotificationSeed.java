package ca.ulaval.glo4003.repul.config.seed.notification;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;

public class TestNotificationSeed extends NotificationSeed {
    public static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    public static final String CLIENT_EMAIL = "alexandra@ulaval.ca";
    public static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");
    public static final DeliveryPersonUniqueIdentifier SECOND_DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("9db0ae10-501c-4efa-b922-41afe2dc75f1");
    public static final String DELIVERY_PERSON_EMAIL = "roger@ulaval.ca";
    public static final String SECOND_DELIVERY_PERSON_EMAIL = "john@ulaval.ca";
    public static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    public static final MealKitUniqueIdentifier TENTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("72112714-4a2a-4384-9d1c-6f0e86c7034a");

    public TestNotificationSeed(UserAccountRepository userAccountRepository, DeliveryPersonAccountRepository deliveryPersonAccountRepository) {
        super(userAccountRepository, deliveryPersonAccountRepository);
    }

    @Override
    public void populate() {
        createUserAccounts();
        createDeliveryPersonAccounts();
    }

    private void createUserAccounts() {
        UserAccount clientAccount = new UserAccount(CLIENT_ID, new Email(CLIENT_EMAIL));
        clientAccount.addMealKit(FIRST_MEAL_KIT_ID);
        clientAccount.addMealKit(TENTH_MEAL_KIT_ID);
        userAccountRepository.save(clientAccount);
    }

    private void createDeliveryPersonAccounts() {
        deliveryPersonAccountRepository.save(new DeliveryPersonAccount(DELIVERY_PERSON_ID, new Email(DELIVERY_PERSON_EMAIL)));
        deliveryPersonAccountRepository.save(new DeliveryPersonAccount(SECOND_DELIVERY_PERSON_ID, new Email(SECOND_DELIVERY_PERSON_EMAIL)));
    }
}
