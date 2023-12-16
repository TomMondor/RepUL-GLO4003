package ca.ulaval.glo4003.repul.config.seed.notification;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.env.EnvParserFactory;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;

public class DemoNotificationSeed extends NotificationSeed {
    private static final EnvParser ENV_PARSER = EnvParserFactory.getEnvParser(".env");

    private static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    private static final String CLIENT_EMAIL =
        ENV_PARSER.readVariable("CLIENT_EMAIL").isBlank() ? "alexandra@ulaval.ca" : ENV_PARSER.readVariable("CLIENT_EMAIL");
    private static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");
    private static final String DELIVERY_PERSON_EMAIL =
        ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL").isBlank() ? "roger@ulaval.ca" : ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL");
    private static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    private static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("39fed158-8b44-4a72-a176-a177012c9c40");
    private static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d955a8bc-126e-47a3-965f-7e84167b4d33");
    private static final MealKitUniqueIdentifier SPORADIC_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("9995a8bc-126e-47a3-965f-7e84167b4999");

    public DemoNotificationSeed(UserAccountRepository userAccountRepository, DeliveryPersonAccountRepository deliveryPersonAccountRepository) {
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
        clientAccount.addMealKit(SECOND_MEAL_KIT_ID);
        clientAccount.addMealKit(THIRD_MEAL_KIT_ID);
        clientAccount.addMealKit(SPORADIC_MEAL_KIT_ID);
        userAccountRepository.save(clientAccount);
    }

    private void createDeliveryPersonAccounts() {
        deliveryPersonAccountRepository.save(new DeliveryPersonAccount(DELIVERY_PERSON_ID, new Email(DELIVERY_PERSON_EMAIL)));
    }
}
