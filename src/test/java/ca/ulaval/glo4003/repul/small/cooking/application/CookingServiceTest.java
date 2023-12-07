package ca.ulaval.glo4003.repul.small.cooking.application;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CookingServiceTest {
    private static final LocalDate A_DATE = LocalDate.of(2022, 1, 1);
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final MealKitUniqueIdentifier A_UNIQUE_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier ANOTHER_UNIQUE_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CookUniqueIdentifier A_COOK_ID = new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generate();
    private static final List<MealKitUniqueIdentifier> MANY_MEAL_KIT_IDS = List.of(A_UNIQUE_MEAL_KIT_ID, ANOTHER_UNIQUE_MEAL_KIT_ID);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final PickedUpCargoEvent PICKED_UP_CARGO_EVENT = new PickedUpCargoEvent(List.of(A_UNIQUE_MEAL_KIT_ID));

    private CookingService cookingService;
    @Mock
    private KitchenPersister kitchenPersister;
    @Mock
    private Kitchen kitchen;
    @Mock
    private RepULEventBus eventBus;

    @BeforeEach
    public void createCookingService() {
        this.cookingService = new CookingService(kitchenPersister, eventBus);
        when(kitchenPersister.get()).thenReturn(kitchen);
    }

    @Test
    public void whenReceivingMealKitInTheKitchen_shouldGetKitchen() {
        cookingService.receiveMealKitInKitchen(A_UNIQUE_MEAL_KIT_ID, A_MEALKIT_TYPE, A_DATE);

        verify(kitchenPersister).get();
    }

    @Test
    public void whenReceivingMealKitInTheKitchen_shouldAddMealKitToKitchen() {
        cookingService.receiveMealKitInKitchen(A_UNIQUE_MEAL_KIT_ID, A_MEALKIT_TYPE, A_DATE);

        verify(kitchen).addMealKit(A_UNIQUE_MEAL_KIT_ID, A_MEALKIT_TYPE, A_DATE);
    }

    @Test
    public void whenReceivingMealKitInTheKitchen_shouldSaveKitchen() {
        cookingService.receiveMealKitInKitchen(A_UNIQUE_MEAL_KIT_ID, A_MEALKIT_TYPE, A_DATE);

        verify(kitchenPersister).save(kitchen);
    }

    @Test
    public void whenGetMealKitsToCook_shouldGetKitchen() {
        cookingService.getMealKitsToCook();

        verify(kitchenPersister).get();
    }

    @Test
    public void whenGetMealKitsToCook_shouldGetMealKitsToCook() {
        cookingService.getMealKitsToCook();

        verify(kitchen).getMealKitsToCook();
    }

    @Test
    public void whenSelect_shouldGetKitchen() {
        cookingService.select(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(kitchenPersister).get();
    }

    @Test
    public void whenSelect_shouldSelectMealKits() {
        cookingService.select(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(kitchen).select(A_COOK_ID, MANY_MEAL_KIT_IDS);
    }

    @Test
    public void whenSelect_shouldSaveKitchen() {
        cookingService.select(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(kitchenPersister).save(kitchen);
    }

    @Test
    public void whenGetSelection_shouldGetKitchen() {
        cookingService.getSelection(A_COOK_ID);

        verify(kitchenPersister).get();
    }

    @Test
    public void whenGetSelection_shouldGetSelectedMealKits() {
        cookingService.getSelection(A_COOK_ID);

        verify(kitchen).getSelection(A_COOK_ID);
    }

    @Test
    public void whenCancelOneSelected_shouldGetKitchen() {
        cookingService.cancelOneSelected(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(kitchenPersister).get();
    }

    @Test
    public void whenCancelOneSelected_shouldCancelOneSelected() {
        cookingService.cancelOneSelected(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(kitchen).cancelOneSelected(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);
    }

    @Test
    public void whenCancelOneSelected_shouldSaveKitchen() {
        cookingService.cancelOneSelected(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(kitchenPersister).save(kitchen);
    }

    @Test
    public void whenCancelAllSelected_shouldGetKitchen() {
        cookingService.cancelAllSelected(A_COOK_ID);

        verify(kitchenPersister).get();
    }

    @Test
    public void whenCancelAllSelected_shouldCancelOneSelected() {
        cookingService.cancelAllSelected(A_COOK_ID);

        verify(kitchen).cancelAllSelected(A_COOK_ID);
    }

    @Test
    public void whenCancelAllSelected_shouldSaveKitchen() {
        cookingService.cancelAllSelected(A_COOK_ID);

        verify(kitchenPersister).save(kitchen);
    }

    @Test
    public void whenConfirmCooked_shouldGetKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(kitchenPersister).get();
    }

    @Test
    public void whenConfirmCooked_shouldConfirmCooked() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(kitchen).confirmCooked(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);
    }

    @Test
    public void whenConfirmCooked_shouldSaveKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(kitchenPersister).save(kitchen);
    }

    @Test
    public void whenConfirmCooked_shouldPublishMealKitsCookedEvent() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);
        ArgumentCaptor<MealKitsCookedEvent> eventCaptor = ArgumentCaptor.forClass(MealKitsCookedEvent.class);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(eventBus).publish(eventCaptor.capture());
        MealKitsCookedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(A_KITCHEN_LOCATION_ID.toString(), publishedEvent.kitchenLocationId);
        assertEquals(List.of(A_UNIQUE_MEAL_KIT_ID), publishedEvent.mealKitIds);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldGetKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(kitchenPersister).get();
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldConfirmCooked() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(kitchen).confirmCooked(A_COOK_ID, MANY_MEAL_KIT_IDS);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldSaveKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(kitchenPersister).save(kitchen);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldPublishMealKitsCookedEvent() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);
        ArgumentCaptor<MealKitsCookedEvent> eventCaptor = ArgumentCaptor.forClass(MealKitsCookedEvent.class);

        cookingService.confirmCooked(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(eventBus).publish(eventCaptor.capture());
        MealKitsCookedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(A_KITCHEN_LOCATION_ID.toString(), publishedEvent.kitchenLocationId);
        assertEquals(MANY_MEAL_KIT_IDS, publishedEvent.mealKitIds);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldSave() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, List.of(A_UNIQUE_MEAL_KIT_ID, ANOTHER_UNIQUE_MEAL_KIT_ID));

        verify(kitchenPersister).save(kitchen);
    }

    @Test
    public void whenRemovingMealKits_shouldGetKitchen() {
        cookingService.giveMealKitToDelivery(List.of(A_UNIQUE_MEAL_KIT_ID));

        verify(kitchenPersister).get();
    }

    @Test
    public void whenGivingMealKitToDelivery_shouldRemoveMealKitsFromKitchen() {
        cookingService.giveMealKitToDelivery(List.of(A_UNIQUE_MEAL_KIT_ID));

        verify(kitchen).removeMealKitsFromKitchen(List.of(A_UNIQUE_MEAL_KIT_ID));
    }

    @Test
    public void whenGivingMealKitToDelivery_shouldSaveKitchen() {
        cookingService.giveMealKitToDelivery(List.of(A_UNIQUE_MEAL_KIT_ID));

        verify(kitchenPersister).save(kitchen);
    }
}
