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

public class TestCookingSeed extends CookingSeed {
    public static final SubscriptionUniqueIdentifier SPORADIC_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7d6e4bb0-ec23-42c1-928a-2704757436be");
    public static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7f8ffb13-56c6-4478-86c2-1f1783993e55");
    public static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("4ba3561d-8ef6-4f8c-a7dc-2e9ebfd23597");
    public static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("9d20441d-9d21-4004-a865-3c35800065f2");
    public static final SubscriptionUniqueIdentifier FOURTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("296f5906-1a9b-4488-96a7-a5dbed0c69c3");
    public static final SubscriptionUniqueIdentifier FIFTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("c4168ad3-e10d-4158-9816-a3efc9a2982b");
    public static final SubscriptionUniqueIdentifier SIXTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("a23a0918-76c4-4d8d-b458-2d6cae32d4bd");
    public static final SubscriptionUniqueIdentifier SEVENTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("97d7fe22-5a58-4c67-9704-b042cea9fd18");
    public static final SubscriptionUniqueIdentifier EIGHTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7a70829c-7e2d-489e-b2e8-6c4f3f9d2b88");
    public static final SubscriptionUniqueIdentifier NINTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("f20c5976-3c5d-490d-b078-877ec3def94c");
    public static final SubscriptionUniqueIdentifier TENTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("bf70c9db-c982-4d1a-9619-bf026e10930c");
    public static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    public static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("39fed158-8b44-4a72-a176-a177012c9c40");
    public static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d955a8bc-126e-47a3-965f-7e84167b4d33");
    public static final MealKitUniqueIdentifier FOURTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("efbab5f6-3ec1-4954-8377-98af932060e1");
    public static final MealKitUniqueIdentifier FIFTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("adcabb17-b711-4cf5-a596-f2a029de7582");
    public static final MealKitUniqueIdentifier SIXTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("0285dc6f-d1cc-4bc0-b1ba-5a4ffe46262e");
    public static final MealKitUniqueIdentifier SEVENTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d61825d4-e270-4c8d-b900-9b9e82fedf7a");
    public static final MealKitUniqueIdentifier EIGHTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("b649b1aa-6e12-4683-a109-2ab148af83d4");
    public static final MealKitUniqueIdentifier NINTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("4df2232c-3b54-4273-8fd7-ea581db0601d");
    public static final MealKitUniqueIdentifier TENTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("72112714-4a2a-4384-9d1c-6f0e86c7034a");
    public static final MealKitUniqueIdentifier SPORADIC_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("f1e4d64b-ade6-47c7-9f29-1729920e4dc9");
    private static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    private static final CookUniqueIdentifier COOK_ID =
        new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom("f36ec513-6522-404e-a2b9-a4202b12c571");

    public TestCookingSeed(KitchenPersister kitchenPersister) {
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
        kitchen.createMealKitInPreparation(FOURTH_MEAL_KIT_ID, FOURTH_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(FIFTH_MEAL_KIT_ID, FIFTH_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(SIXTH_MEAL_KIT_ID, SIXTH_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(SEVENTH_MEAL_KIT_ID, SEVENTH_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(EIGHTH_MEAL_KIT_ID, EIGHTH_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(NINTH_MEAL_KIT_ID, NINTH_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(TENTH_MEAL_KIT_ID, TENTH_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.of(DeliveryLocationId.VACHON));
        kitchen.createMealKitInPreparation(SPORADIC_MEAL_KIT_ID, SPORADIC_SUBSCRIPTION_ID, CLIENT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1),
            Optional.empty());
    }
}
