package ca.ulaval.glo4003.repul.config.seed.delivery;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemPersister;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;

public class DemoDeliverySeed extends DeliverySeed {
    private static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    private static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom("08d87ed8-147b-4b1a-bd58-e7955e7c4344");
    private static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7f8ffb13-56c6-4478-86c2-1f1783993e55");
    private static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("4ba3561d-8ef6-4f8c-a7dc-2e9ebfd23597");
    private static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("9d20441d-9d21-4004-a865-3c35800065f2");
    private static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("39fed158-8b44-4a72-a176-a177012c9c40");
    private static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d955a8bc-126e-47a3-965f-7e84167b4d33");
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");

    public DemoDeliverySeed(DeliverySystemPersister deliverySystemPersister, LocationsCatalog locationsCatalog) {
        super(deliverySystemPersister, locationsCatalog);
    }

    @Override
    public void populate() {
        DeliverySystem deliverySystem = new DeliverySystem(locationsCatalog);
        createDeliveryPerson(deliverySystem);
        createMealKits(deliverySystem);
        deliverySystemPersister.save(deliverySystem);
    }

    private void createDeliveryPerson(DeliverySystem deliverySystem) {
        deliverySystem.addDeliveryPerson(DELIVERY_PERSON_ID);
    }

    private void createMealKits(DeliverySystem deliverySystem) {
        deliverySystem.createPendingMealKit(CLIENT_ID, FIRST_SUBSCRIPTION_ID, FIRST_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, SECOND_SUBSCRIPTION_ID, SECOND_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(CLIENT_ID, THIRD_SUBSCRIPTION_ID, THIRD_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
    }
}
