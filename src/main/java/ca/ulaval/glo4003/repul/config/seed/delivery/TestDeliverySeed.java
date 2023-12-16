package ca.ulaval.glo4003.repul.config.seed.delivery;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;

public class TestDeliverySeed extends DeliverySeed {
    private static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7f8ffb13-56c6-4478-86c2-1f1783993e55");
    private static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("4ba3561d-8ef6-4f8c-a7dc-2e9ebfd23597");
    private static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("9d20441d-9d21-4004-a865-3c35800065f2");
    private static final SubscriptionUniqueIdentifier FOURTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("296f5906-1a9b-4488-96a7-a5dbed0c69c3");
    private static final SubscriptionUniqueIdentifier FIFTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("c4168ad3-e10d-4158-9816-a3efc9a2982b");
    private static final SubscriptionUniqueIdentifier SIXTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("a23a0918-76c4-4d8d-b458-2d6cae32d4bd");
    private static final SubscriptionUniqueIdentifier SEVENTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("97d7fe22-5a58-4c67-9704-b042cea9fd18");
    private static final SubscriptionUniqueIdentifier EIGHTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7a70829c-7e2d-489e-b2e8-6c4f3f9d2b88");
    private static final SubscriptionUniqueIdentifier NINTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("f20c5976-3c5d-490d-b078-877ec3def94c");
    private static final SubscriptionUniqueIdentifier TENTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("bf70c9db-c982-4d1a-9619-bf026e10930c");
    private static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    private static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("39fed158-8b44-4a72-a176-a177012c9c40");
    private static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d955a8bc-126e-47a3-965f-7e84167b4d33");
    private static final MealKitUniqueIdentifier FOURTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("efbab5f6-3ec1-4954-8377-98af932060e1");
    private static final MealKitUniqueIdentifier FIFTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("adcabb17-b711-4cf5-a596-f2a029de7582");
    private static final MealKitUniqueIdentifier SIXTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("0285dc6f-d1cc-4bc0-b1ba-5a4ffe46262e");
    private static final MealKitUniqueIdentifier SEVENTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d61825d4-e270-4c8d-b900-9b9e82fedf7a");
    private static final MealKitUniqueIdentifier EIGHTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("b649b1aa-6e12-4683-a109-2ab148af83d4");
    private static final MealKitUniqueIdentifier NINTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("4df2232c-3b54-4273-8fd7-ea581db0601d");
    private static final MealKitUniqueIdentifier TENTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("72112714-4a2a-4384-9d1c-6f0e86c7034a");
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    private static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");
    private static final DeliveryPersonUniqueIdentifier SECOND_DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("9db0ae10-501c-4efa-b922-41afe2dc75f1");

    public TestDeliverySeed(DeliverySystemPersister deliverySystemPersister, LocationsCatalog locationsCatalog) {
        super(deliverySystemPersister, locationsCatalog);
    }

    @Override
    public void populate() {
        DeliverySystem deliverySystem = new DeliverySystem(locationsCatalog);
        createDeliveryPerson(deliverySystem);
        createMealKits(deliverySystem);
        createCargos(deliverySystem);
        deliverySystemPersister.save(deliverySystem);
    }

    private void createDeliveryPerson(DeliverySystem deliverySystem) {
        deliverySystem.addDeliveryPerson(DELIVERY_PERSON_ID);
        deliverySystem.addDeliveryPerson(SECOND_DELIVERY_PERSON_ID);
    }

    private void createMealKits(DeliverySystem deliverySystem) {
        deliverySystem.createPendingMealKit(CLIENT_ID, FIRST_SUBSCRIPTION_ID, FIRST_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, SECOND_SUBSCRIPTION_ID, SECOND_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, THIRD_SUBSCRIPTION_ID, THIRD_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, FOURTH_SUBSCRIPTION_ID, FOURTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, FIFTH_SUBSCRIPTION_ID, FIFTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, SIXTH_SUBSCRIPTION_ID, SIXTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, SEVENTH_SUBSCRIPTION_ID, SEVENTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, EIGHTH_SUBSCRIPTION_ID, EIGHTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, NINTH_SUBSCRIPTION_ID, NINTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, TENTH_SUBSCRIPTION_ID, TENTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
    }

    private void createCargos(DeliverySystem deliverySystem) {
        deliverySystem.receiveReadyToBeDeliveredMealKits(KitchenLocationId.DESJARDINS, List.of(TENTH_MEAL_KIT_ID));
    }
}
