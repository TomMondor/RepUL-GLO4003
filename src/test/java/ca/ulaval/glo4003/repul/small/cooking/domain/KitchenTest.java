package ca.ulaval.glo4003.repul.small.cooking.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.domain.Cook.Cook;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitCannotBeSelectedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotForKitchenPickUpException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotInSelectionException;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKitFactory;
import ca.ulaval.glo4003.repul.cooking.domain.recipe.Recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class KitchenTest {
    private static final MealKitUniqueIdentifier A_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier ANOTHER_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final CookUniqueIdentifier A_COOK_ID = new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final Optional<DeliveryLocationId> A_DELIVERY_LOCATION_ID = Optional.of(DeliveryLocationId.DESJARDINS);
    private static final SubscriberUniqueIdentifier ANOTHER_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();

    private Kitchen kitchen;

    @BeforeEach
    public void createKitchen() {
        Map<MealKitType, List<Recipe>> recipes = new HashMap<>();
        recipes.put(MealKitType.STANDARD, List.of());
        RecipesCatalog.getInstance().setRecipes(recipes);
        this.kitchen = new Kitchen(new MealKitFactory());
        Cook cook = new Cook(A_COOK_ID);
        this.kitchen.hireCook(cook);
    }

    @Test
    public void whenAddingMealKit_shouldAddMealKit() {
        kitchen.createMealKitInPreparation(A_MEALKIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEALKIT_TYPE, TOMORROW, A_DELIVERY_LOCATION_ID);

        assertEquals(1, kitchen.getMealKitsToPrepare().size());
    }

    @Test
    public void givenTwoMealKitsAndOneNotSelected_whenGetMealKitsToPrepare_shouldReturnOneMealKit() {
        addMealKit(A_MEALKIT_ID, false);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        List<MealKit> mealKitsToPrepare = kitchen.getMealKitsToPrepare();

        assertEquals(1, mealKitsToPrepare.size());
        assertEquals(A_MEALKIT_ID, mealKitsToPrepare.get(0).getMealKitId());
    }

    @Test
    public void givenSelectedMealKit_whenSelect_shouldThrowMealKitCannotBeSelectedException() {
        addMealKit(A_MEALKIT_ID, true);

        assertThrows(MealKitCannotBeSelectedException.class, () -> kitchen.select(A_COOK_ID, List.of(A_MEALKIT_ID)));
    }

    @Test
    public void givenUnselectedMealKit_whenSelect_shouldSelectMealKitForCook() {
        addMealKit(A_MEALKIT_ID, false);

        kitchen.select(A_COOK_ID, List.of(A_MEALKIT_ID));

        assertEquals(A_MEALKIT_ID, kitchen.getSelection(A_COOK_ID).get(0));
    }

    @Test
    public void givenInvalidMealKitId_whenSelect_shouldThrowMealKitCannotBeSelectedException() {
        assertThrows(MealKitCannotBeSelectedException.class, () -> kitchen.select(A_COOK_ID, List.of(A_MEALKIT_ID)));
    }

    @Test
    public void givenSelectedMealKit_whenGetSelection_shouldReturnMealKit() {
        addMealKit(A_MEALKIT_ID, true);

        List<MealKitUniqueIdentifier> selection = kitchen.getSelection(A_COOK_ID);

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

        assertThrows(MealKitNotInSelectionException.class, () -> kitchen.confirmPreparation(A_COOK_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitInSelection_whenConfirmOneCooked_shouldSetMealKitAsCooked() {
        addMealKit(A_MEALKIT_ID, true);

        kitchen.confirmPreparation(A_COOK_ID, A_MEALKIT_ID);

        List<MealKitUniqueIdentifier> mealKitsToPrepareIds = kitchen.getMealKitsToPrepare().stream().map(MealKit::getMealKitId).toList();
        assertFalse(mealKitsToPrepareIds.contains(A_MEALKIT_ID));
        assertFalse(mealKitsToPrepareIds.contains(ANOTHER_MEALKIT_ID));
        assertFalse(kitchen.getSelection(A_COOK_ID).contains(A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitInSelection_whenConfirmOneCooked_shouldReturnCookedMealKit() {
        addMealKit(A_MEALKIT_ID, true);

        MealKit actualMealKit = kitchen.confirmPreparation(A_COOK_ID, A_MEALKIT_ID);

        assertEquals(A_MEALKIT_ID, actualMealKit.getMealKitId());
        assertTrue(actualMealKit.isPrepared());
    }

    @Test
    public void givenMealKitNotInSelection_whenConfirmMultipleCooked_shouldThrowMealKitNotInSelectionException() {
        addMealKit(A_MEALKIT_ID, false);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        assertThrows(MealKitNotInSelectionException.class, () -> kitchen.confirmPreparation(A_COOK_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID)));
    }

    @Test
    public void givenMealKitsInSelection_whenConfirmMultipleCooked_shouldSetMealKitAsCooked() {
        addMealKit(A_MEALKIT_ID, true);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        kitchen.confirmPreparation(A_COOK_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));

        List<MealKitUniqueIdentifier> mealKitsToPrepareIds = kitchen.getMealKitsToPrepare().stream().map(MealKit::getMealKitId).toList();
        assertFalse(mealKitsToPrepareIds.contains(A_MEALKIT_ID));
        assertFalse(mealKitsToPrepareIds.contains(ANOTHER_MEALKIT_ID));
        assertFalse(kitchen.getSelection(A_COOK_ID).contains(A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitsInSelection_whenConfirmMultipleCooked_shouldReturnCookedMealKits() {
        addMealKit(A_MEALKIT_ID, true);
        addMealKit(ANOTHER_MEALKIT_ID, true);

        List<MealKit> actualMealKits = kitchen.confirmPreparation(A_COOK_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));

        assertTrue(actualMealKits.stream().anyMatch(mealKit -> mealKit.getMealKitId().equals(A_MEALKIT_ID) && mealKit.isPrepared()));
        assertTrue(actualMealKits.stream().anyMatch(mealKit -> mealKit.getMealKitId().equals(ANOTHER_MEALKIT_ID) && mealKit.isPrepared()));
    }

    @Test
    public void whenRemovingMealKitsFromKitchen_shouldNotBePossibleToUnconfirmAnymore() {
        addMealKit(A_MEALKIT_ID, true);
        kitchen.confirmPreparation(A_COOK_ID, List.of(A_MEALKIT_ID));

        kitchen.pickUpMealKitsForDelivery(List.of(A_MEALKIT_ID));

        assertThrows(MealKitNotFoundException.class, () -> kitchen.unconfirmPreparation(A_COOK_ID, A_MEALKIT_ID));
    }

    @Test
    public void whenPickingUpNonDeliverableMealKit_shouldReturnIt() {
        addAndCookNonDeliverableMealKit(A_MEALKIT_ID, A_SUBSCRIBER_ID);

        MealKit actualMealKit = kitchen.pickUpNonDeliverableMealKit(A_SUBSCRIBER_ID, A_MEALKIT_ID);

        assertEquals(A_MEALKIT_ID, actualMealKit.getMealKitId());
        assertTrue(actualMealKit.isPrepared());
    }

    @Test
    public void whenPickingUpNonDeliverableMealKit_shouldBeRemovedFromKitchenAndSoNotPossibleToUnconfirmPreparationAnymore() {
        addAndCookNonDeliverableMealKit(A_MEALKIT_ID, A_SUBSCRIBER_ID);

        kitchen.pickUpNonDeliverableMealKit(A_SUBSCRIBER_ID, A_MEALKIT_ID);

        assertThrows(MealKitNotFoundException.class, () -> kitchen.unconfirmPreparation(A_COOK_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitNotInKitchen_whenPickingUpNonDeliverableMealKit_shouldThrowMealKitNotFoundException() {
        assertThrows(MealKitNotFoundException.class, () -> kitchen.pickUpNonDeliverableMealKit(A_SUBSCRIBER_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitForOtherSubscriber_whenPickingUpNonDeliverableMealKit_shouldThrowMealKitNotFoundException() {
        addAndCookNonDeliverableMealKit(A_MEALKIT_ID, A_SUBSCRIBER_ID);

        assertThrows(MealKitNotFoundException.class, () -> kitchen.pickUpNonDeliverableMealKit(ANOTHER_SUBSCRIBER_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitNotCookedYet_whenPickingUpNonDeliverableMealKit_shouldThrowMealKitNotFoundException() {
        kitchen.createMealKitInPreparation(A_MEALKIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEALKIT_TYPE, TOMORROW, Optional.empty());

        assertThrows(MealKitNotFoundException.class, () -> kitchen.pickUpNonDeliverableMealKit(A_SUBSCRIBER_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenMealKitForDelivery_whenPickingUpNonDeliverableMealKit_shouldThrowMealKitNotForKitchenPickUpException() {
        kitchen.createMealKitInPreparation(A_MEALKIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEALKIT_TYPE, TOMORROW, A_DELIVERY_LOCATION_ID);
        kitchen.select(A_COOK_ID, List.of(A_MEALKIT_ID));
        kitchen.confirmPreparation(A_COOK_ID, List.of(A_MEALKIT_ID));

        assertThrows(MealKitNotForKitchenPickUpException.class, () -> kitchen.pickUpNonDeliverableMealKit(A_SUBSCRIBER_ID, A_MEALKIT_ID));
    }

    private void addMealKit(MealKitUniqueIdentifier mealKitId, boolean selected) {
        kitchen.createMealKitInPreparation(mealKitId, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEALKIT_TYPE, TOMORROW, A_DELIVERY_LOCATION_ID);

        if (selected) {
            kitchen.select(A_COOK_ID, List.of(mealKitId));
        }
    }

    private void addAndCookNonDeliverableMealKit(MealKitUniqueIdentifier mealKitId, SubscriberUniqueIdentifier subscriberId) {
        kitchen.createMealKitInPreparation(mealKitId, A_SUBSCRIPTION_ID, subscriberId, A_MEALKIT_TYPE, TOMORROW, Optional.empty());
        kitchen.select(A_COOK_ID, List.of(mealKitId));
        kitchen.confirmPreparation(A_COOK_ID, List.of(mealKitId));
    }
}
