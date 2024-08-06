package ca.ulaval.glo4003.repul.config.seed.cooking;

import java.time.LocalDate;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.domain.Cook.Cook;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKitFactory;

public class DemoCookingSeed extends CookingSeed {
    private static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    private static final CookUniqueIdentifier COOK_ID =
        new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom("f36ec513-6522-404e-a2b9-a4202b12c571");
    private static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7f8ffb13-56c6-4478-86c2-1f1783993e55");
    private static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("4ba3561d-8ef6-4f8c-a7dc-2e9ebfd23597");
    private static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("9d20441d-9d21-4004-a865-3c35800065f2");
    private static final SubscriptionUniqueIdentifier SPORADIC_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("1235a8bc-126e-47a3-965f-7e84167b4123");
    private static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    private static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("39fed158-8b44-4a72-a176-a177012c9c40");
    private static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d955a8bc-126e-47a3-965f-7e84167b4d33");
    private static final MealKitUniqueIdentifier SPORADIC_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("9995a8bc-126e-47a3-965f-7e84167b4999");

    public DemoCookingSeed(KitchenPersister kitchenPersister) {
        super(kitchenPersister);
    }

    @Override
    public void populate() {
        Kitchen kitchen = new Kitchen(new MealKitFactory());
        createCook(kitchen);
        createMealKits(kitchen);
        kitchenPersister.save(kitchen);
    }

    private void createCook(Kitchen kitchen) {
        kitchen.hireCook(new Cook(COOK_ID));
    }

    private void createMealKits(Kitchen kitchen) {
        kitchen.createMealKitInPreparation(FIRST_MEAL_KIT_ID, FIRST_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(SECOND_MEAL_KIT_ID, SECOND_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(THIRD_MEAL_KIT_ID, THIRD_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(SPORADIC_MEAL_KIT_ID, SPORADIC_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.empty());
    }
}