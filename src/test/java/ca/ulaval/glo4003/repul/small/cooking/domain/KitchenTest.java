package ca.ulaval.glo4003.repul.small.cooking.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitAlreadySelectedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotInSelectionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class KitchenTest {
    private static final UniqueIdentifier A_MEALKIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier ANOTHER_MEALKIT_ID = new UniqueIdentifierFactory().generate();
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final UniqueIdentifier A_COOK_ID = new UniqueIdentifierFactory().generate();

    private Kitchen kitchen;

    @BeforeEach
    public void createKitchen() {
        Map<MealKitType, List<Recipe>> recipes = new HashMap<>();
        recipes.put(MealKitType.STANDARD, List.of());
        this.kitchen = new Kitchen(new RecipesCatalog(recipes));
    }

    @Test
    public void whenAddMealKit_shouldAddMealKit() {
        kitchen.addMealKit(A_MEALKIT_ID, A_MEALKIT_TYPE, TOMORROW);

        assertEquals(1, kitchen.getMealKitsToCook().size());
    }

    @Test
    public void givenTwoMealKitsAndOneNotSelected_whenGetMealKitsToCook_shouldReturnOneMealKit() {
        addMealKit(A_MEALKIT_ID, false);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        List<MealKit> mealKitsToCook = kitchen.getMealKitsToCook();

        assertEquals(1, mealKitsToCook.size());
        assertEquals(A_MEALKIT_ID, mealKitsToCook.get(0).getMealKitId());
    }

    @Test
    public void givenSelectedMealKit_whenSelect_shouldThrowMealKitAlreadySelectedException() {
        addMealKit(A_MEALKIT_ID, true);

        assertThrows(MealKitAlreadySelectedException.class, () -> kitchen.select(A_COOK_ID, List.of(A_MEALKIT_ID)));
    }

    @Test
    public void givenUnselectedMealKit_whenSelect_shouldSelectMealKitForCook() {
        addMealKit(A_MEALKIT_ID, false);

        kitchen.select(A_COOK_ID, List.of(A_MEALKIT_ID));

        assertEquals(A_MEALKIT_ID, kitchen.getSelection(A_COOK_ID).get(0));
    }

    @Test
    public void givenInvalidMealKitId_whenSelect_shouldThrowMealKitNotFoundException() {
        assertThrows(MealKitNotFoundException.class, () -> kitchen.select(A_COOK_ID, List.of(A_MEALKIT_ID)));
    }

    @Test
    public void givenSelectedMealKit_whenGetSelection_shouldReturnMealKit() {
        addMealKit(A_MEALKIT_ID, true);

        List<UniqueIdentifier> selection = kitchen.getSelection(A_COOK_ID);

        assertEquals(A_MEALKIT_ID, selection.get(0));
    }

    @Test
    public void givenInvalidMealKitId_whenCancelOneSelected_shouldThrowMealKitNotInSelectionException() {
        assertThrows(MealKitNotInSelectionException.class, () -> kitchen.cancelOneSelected(A_COOK_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenSelectedMealKit_whenCancelOneSelected_shouldUnselectMealKit() {
        addMealKit(A_MEALKIT_ID, true);

        kitchen.cancelOneSelected(A_COOK_ID, A_MEALKIT_ID);

        assertEquals(0, kitchen.getSelection(A_COOK_ID).size());
    }

    @Test
    public void givenSelectedMealKits_whenCancelAllSelected_shouldUnselectMealKits() {
        addMealKit(A_MEALKIT_ID, true);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        kitchen.cancelAllSelected(A_COOK_ID);

        assertEquals(0, kitchen.getSelection(A_COOK_ID).size());
    }

    @Test
    public void givenMealKitNotInSelection_whenConfirmOneCooked_shouldThrowMealKitNotInSelectionException() {
        addMealKit(A_MEALKIT_ID, false);

        assertThrows(MealKitNotInSelectionException.class, () -> kitchen.confirmCooked(A_COOK_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitInSelection_whenConfirmOneCooked_shouldSetMealKitAsCooked() {
        addMealKit(A_MEALKIT_ID, true);

        kitchen.confirmCooked(A_COOK_ID, A_MEALKIT_ID);

        List<UniqueIdentifier> mealKitsToCookIds = kitchen.getMealKitsToCook().stream().map(MealKit::getMealKitId).toList();
        assertFalse(mealKitsToCookIds.contains(A_MEALKIT_ID));
        assertFalse(mealKitsToCookIds.contains(ANOTHER_MEALKIT_ID));
        assertFalse(kitchen.getSelection(A_COOK_ID).contains(A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitNotInSelection_whenConfirmMultipleCooked_shouldThrowMealKitNotInSelectionException() {
        addMealKit(A_MEALKIT_ID, false);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        assertThrows(MealKitNotInSelectionException.class, () -> kitchen.confirmCooked(A_COOK_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID)));
    }

    @Test
    public void givenMealKitsInSelection_whenConfirmMultipleCooked_shouldSetMealKitAsCooked() {
        addMealKit(A_MEALKIT_ID, true);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        kitchen.confirmCooked(A_COOK_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));

        List<UniqueIdentifier> mealKitsToCookIds = kitchen.getMealKitsToCook().stream().map(MealKit::getMealKitId).toList();
        assertFalse(mealKitsToCookIds.contains(A_MEALKIT_ID));
        assertFalse(mealKitsToCookIds.contains(ANOTHER_MEALKIT_ID));
        assertFalse(kitchen.getSelection(A_COOK_ID).contains(A_MEALKIT_ID));
    }

    @Test
    public void whenRemovingMealKitsFromKitchen_shouldNotBePossibleToRecallAnymore() {
        addMealKit(A_MEALKIT_ID, true);
        kitchen.confirmCooked(A_COOK_ID, List.of(A_MEALKIT_ID));

        kitchen.removeMealKitsFromKitchen(List.of(A_MEALKIT_ID));

        assertThrows(MealKitNotFoundException.class, () -> kitchen.recallCooked(A_COOK_ID, A_MEALKIT_ID));
    }

    private void addMealKit(UniqueIdentifier mealKitId, boolean selected) {
        kitchen.addMealKit(mealKitId, A_MEALKIT_TYPE, TOMORROW);

        if (selected) {
            kitchen.select(A_COOK_ID, List.of(mealKitId));
        }
    }
}
