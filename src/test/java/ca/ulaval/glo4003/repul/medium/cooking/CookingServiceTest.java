package ca.ulaval.glo4003.repul.medium.cooking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;
import ca.ulaval.glo4003.repul.cooking.infrastructure.InMemoryKitchenRepository;

import static org.junit.jupiter.api.Assertions.*;

public class CookingServiceTest {
    private static final UniqueIdentifier DEFAULT_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_COOK_ID = new UniqueIdentifierFactory().generate();
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(1);
    private static final RecipesCatalog A_RECIPES_CATALOG = new RecipesCatalog(Map.of(A_MEAL_KIT_TYPE, new ArrayList<>()));
    private KitchenRepository kitchenRepository;
    private RepULEventBus eventBus;
    private CookingService cookingService;

    @BeforeEach
    public void createCookingService() {
        kitchenRepository = new InMemoryKitchenRepository();
        kitchenRepository.save(createKitchenWithAnUnselectedMealKit());
        eventBus = new GuavaEventBus();
        cookingService = new CookingService(kitchenRepository, eventBus);
    }

    @Test
    public void whenSelectingMealKitsToCook_shouldPersistSelection() {
        assertEquals(0, cookingService.getSelection(A_COOK_ID).size());

        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));

        assertEquals(1, cookingService.getSelection(A_COOK_ID).size());
    }

    @Test
    public void givenASelectedMealKit_whenUnselectingMealKit_ShouldPersistDeselection() {
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));
        assertEquals(1, cookingService.getSelection(A_COOK_ID).size());

        cookingService.cancelOneSelected(A_COOK_ID, DEFAULT_MEAL_KIT_ID);

        assertEquals(0, cookingService.getSelection(A_COOK_ID).size());
    }

    @Test
    public void givenASelectedMealKit_whenUnselectingAllMealKits_ShouldPersistDeselection() {
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));
        assertEquals(1, cookingService.getSelection(A_COOK_ID).size());

        cookingService.cancelAllSelected(A_COOK_ID);

        assertEquals(0, cookingService.getSelection(A_COOK_ID).size());
    }

    @Test
    public void whenConfirmingCooked_shouldRemoveMealKitFromSelection() {
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));
        assertEquals(1, cookingService.getSelection(A_COOK_ID).size());

        cookingService.confirmCooked(A_COOK_ID, DEFAULT_MEAL_KIT_ID);

        assertEquals(0, cookingService.getSelection(A_COOK_ID).size());
    }

    private Kitchen createKitchenWithAnUnselectedMealKit() {
        Kitchen kitchen = new Kitchen(A_RECIPES_CATALOG);
        kitchen.addMealKit(DEFAULT_MEAL_KIT_ID, A_MEAL_KIT_TYPE, A_DELIVERY_DATE);
        return kitchen;
    }
}
