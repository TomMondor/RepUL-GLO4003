package ca.ulaval.glo4003.repul.medium.cooking.application;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotForKitchenPickUpException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKitFactory;
import ca.ulaval.glo4003.repul.cooking.infrastructure.InMemoryKitchenPersister;

import static org.junit.jupiter.api.Assertions.*;

public class CookingServiceTest {
    private static final MealKitUniqueIdentifier DEFAULT_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CookUniqueIdentifier A_COOK_ID = TestApplicationContext.COOK_ID;
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(1);
    private static final Optional<DeliveryLocationId> A_DELIVERY_LOCATION_ID = Optional.of(DeliveryLocationId.DESJARDINS);
    private KitchenPersister kitchenPersister;
    private RepULEventBus eventBus;
    private CookingService cookingService;

    @BeforeEach
    public void createCookingService() {
        initializeRecipesCatalog();

        kitchenPersister = new InMemoryKitchenPersister();
        kitchenPersister.save(createKitchenWithAnUnselectedMealKit());
        eventBus = new GuavaEventBus();
        cookingService = new CookingService(kitchenPersister, eventBus);
    }

    @Test
    public void whenSelectingMealKitsToCook_shouldPersistSelection() {
        assertEquals(0, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());

        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));

        assertEquals(1, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());
        assertEquals(List.of(DEFAULT_MEAL_KIT_ID.getUUID().toString()), cookingService.getSelection(A_COOK_ID).mealKitSelectionIds());
    }

    @Test
    public void givenASelectedMealKit_whenUnselectingMealKit_ShouldPersistDeselection() {
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));
        assertEquals(1, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());

        cookingService.cancelOneSelected(A_COOK_ID, DEFAULT_MEAL_KIT_ID);

        assertEquals(0, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());
    }

    @Test
    public void givenASelectedMealKit_whenUnselectingAllMealKits_ShouldPersistDeselection() {
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));
        assertEquals(1, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());

        cookingService.cancelAllSelected(A_COOK_ID);

        assertEquals(0, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());
    }

    @Test
    public void whenConfirmingCooked_shouldRemoveMealKitFromSelection() {
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));
        assertEquals(1, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());

        cookingService.confirmCooked(A_COOK_ID, DEFAULT_MEAL_KIT_ID);

        assertEquals(0, cookingService.getSelection(A_COOK_ID).mealKitSelectionIds().size());
    }

    @Test
    public void givenCookedMealKit_whenGivingMealKitToDelivery_shouldRemoveMealKitFromKitchen() {
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));
        cookingService.confirmCooked(A_COOK_ID, DEFAULT_MEAL_KIT_ID);

        cookingService.giveMealKitsToDelivery(List.of(DEFAULT_MEAL_KIT_ID));

        assertThrows(MealKitNotFoundException.class, () -> cookingService.recallCooked(A_COOK_ID, DEFAULT_MEAL_KIT_ID));
    }

    @Test
    public void givenMealKitForInKitchenPickUp_whenConfirmingCooked_shouldBeAbleToPickItUp() {
        cookingService.createMealKitInPreparation(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE, A_DELIVERY_DATE, Optional.empty());
        cookingService.select(A_COOK_ID, List.of(A_MEAL_KIT_ID));

        cookingService.confirmCooked(A_COOK_ID, A_MEAL_KIT_ID);

        assertDoesNotThrow(() -> cookingService.pickupNonDeliverableMealKit(A_SUBSCRIBER_ID, A_MEAL_KIT_ID));
    }

    @Test
    public void givenMealKitForDelivery_whenConfirmingCooked_shouldNotBeAbleToPickItUpInKitchen() {
        cookingService.createMealKitInPreparation(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE, A_DELIVERY_DATE, A_DELIVERY_LOCATION_ID);
        cookingService.select(A_COOK_ID, List.of(A_MEAL_KIT_ID));

        cookingService.confirmCooked(A_COOK_ID, A_MEAL_KIT_ID);

        assertThrows(MealKitNotForKitchenPickUpException.class, () -> cookingService.pickupNonDeliverableMealKit(A_SUBSCRIBER_ID, A_MEAL_KIT_ID));
    }

    private Kitchen createKitchenWithAnUnselectedMealKit() {
        Kitchen kitchen = new Kitchen(new MealKitFactory());
        kitchen.hireCook(TestApplicationContext.cook);
        kitchen.createMealKitInPreparation(DEFAULT_MEAL_KIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE, A_DELIVERY_DATE, A_DELIVERY_LOCATION_ID);
        return kitchen;
    }

    private void initializeRecipesCatalog() {
        Map<MealKitType, List<Recipe>> recipes = new HashMap<>();
        recipes.put(MealKitType.STANDARD, List.of());
        RecipesCatalog.getInstance().setRecipes(recipes);
    }
}
