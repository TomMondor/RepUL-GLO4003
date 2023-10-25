package ca.ulaval.glo4003.repul.small.cooking.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;
import ca.ulaval.glo4003.repul.cooking.domain.exception.KitchenNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CookingServiceTest {
    private static final LocalDate A_DATE = LocalDate.of(2022, 1, 1);
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final UniqueIdentifier A_UNIQUE_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_UNIQUE_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_COOK_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final List<UniqueIdentifier> MANY_IDS = List.of(A_UNIQUE_ID, ANOTHER_UNIQUE_ID);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("DESJARDINS");
    private static final MealKitConfirmedEvent MEALKIT_CONFIRMED_EVENT =
        new MealKitConfirmedEvent(A_UNIQUE_ID, A_UNIQUE_ID, A_UNIQUE_ID, A_MEALKIT_TYPE, null, A_DATE);

    private CookingService cookingService;
    @Mock
    private KitchenRepository kitchenRepository;
    @Mock
    private Kitchen kitchen;
    @Mock
    private RepULEventBus eventBus;

    @BeforeEach
    public void createCookingService() {
        this.cookingService = new CookingService(kitchenRepository, eventBus);
        when(kitchenRepository.get()).thenReturn(Optional.of(kitchen));
    }

    @Test
    public void whenHandleMealKitConfirmedEvent_shouldGetKitchen() {
        cookingService.handleMealKitConfirmedEvent(MEALKIT_CONFIRMED_EVENT);

        verify(kitchenRepository).get();
    }

    @Test
    public void whenHandleMealKitConfirmedEvent_shouldAddMealKit() {
        cookingService.handleMealKitConfirmedEvent(MEALKIT_CONFIRMED_EVENT);

        verify(kitchen).addMealKit(A_UNIQUE_ID, A_MEALKIT_TYPE, A_DATE);
    }

    @Test
    public void whenHandleMealKitConfirmedEvent_shouldSaveOrUpdateKitchen() {
        cookingService.handleMealKitConfirmedEvent(MEALKIT_CONFIRMED_EVENT);

        verify(kitchenRepository).saveOrUpdate(kitchen);
    }

    @Test
    public void givenNoKitchen_whenGettingKitchenFromRepository_shouldThrowKitchenNotFoundException() {
        when(kitchenRepository.get()).thenReturn(Optional.empty());

        assertThrows(KitchenNotFoundException.class, () -> cookingService.getMealKitsToCook());
    }

    @Test
    public void whenGetMealKitsToCook_shouldGetKitchen() {
        cookingService.getMealKitsToCook();

        verify(kitchenRepository).get();
    }

    @Test
    public void whenGetMealKitsToCook_shouldGetMealKitsToCook() {
        cookingService.getMealKitsToCook();

        verify(kitchen).getMealKitsToCook();
    }

    @Test
    public void whenSelect_shouldGetKitchen() {
        cookingService.select(A_COOK_ID, MANY_IDS);

        verify(kitchenRepository).get();
    }

    @Test
    public void whenSelect_shouldSelectMealKits() {
        cookingService.select(A_COOK_ID, MANY_IDS);

        verify(kitchen).select(A_COOK_ID, MANY_IDS);
    }

    @Test
    public void whenSelect_shouldSaveOrUpdateKitchen() {
        cookingService.select(A_COOK_ID, MANY_IDS);

        verify(kitchenRepository).saveOrUpdate(kitchen);
    }

    @Test
    public void whenGetSelection_shouldGetKitchen() {
        cookingService.getSelection(A_COOK_ID);

        verify(kitchenRepository).get();
    }

    @Test
    public void whenGetSelection_shouldGetSelectedMealKits() {
        cookingService.getSelection(A_COOK_ID);

        verify(kitchen).getSelection(A_COOK_ID);
    }

    @Test
    public void whenCancelOneSelected_shouldGetKitchen() {
        cookingService.cancelOneSelected(A_COOK_ID, A_UNIQUE_ID);

        verify(kitchenRepository).get();
    }

    @Test
    public void whenCancelOneSelected_shouldCancelOneSelected() {
        cookingService.cancelOneSelected(A_COOK_ID, A_UNIQUE_ID);

        verify(kitchen).cancelOneSelected(A_COOK_ID, A_UNIQUE_ID);
    }

    @Test
    public void whenCancelOneSelected_shouldSaveOrUpdateKitchen() {
        cookingService.cancelOneSelected(A_COOK_ID, A_UNIQUE_ID);

        verify(kitchenRepository).saveOrUpdate(kitchen);
    }

    @Test
    public void whenCancelAllSelected_shouldGetKitchen() {
        cookingService.cancelAllSelected(A_COOK_ID);

        verify(kitchenRepository).get();
    }

    @Test
    public void whenCancelAllSelected_shouldCancelOneSelected() {
        cookingService.cancelAllSelected(A_COOK_ID);

        verify(kitchen).cancelAllSelected(A_COOK_ID);
    }

    @Test
    public void whenCancelAllSelected_shouldSaveOrUpdateKitchen() {
        cookingService.cancelAllSelected(A_COOK_ID);

        verify(kitchenRepository).saveOrUpdate(kitchen);
    }

    @Test
    public void whenConfirmCooked_shouldGetKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_ID);

        verify(kitchenRepository).get();
    }

    @Test
    public void whenConfirmCooked_shouldConfirmCooked() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_ID);

        verify(kitchen).confirmCooked(A_COOK_ID, A_UNIQUE_ID);
    }

    @Test
    public void whenConfirmCooked_shouldSaveOrUpdateKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_ID);

        verify(kitchenRepository).saveOrUpdate(kitchen);
    }

    @Test
    public void whenConfirmCooked_shouldPublishMealKitsCookedEvent() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);
        ArgumentCaptor<MealKitsCookedEvent> eventCaptor = ArgumentCaptor.forClass(MealKitsCookedEvent.class);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_ID);

        verify(eventBus).publish(eventCaptor.capture());
        MealKitsCookedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(A_KITCHEN_LOCATION_ID.value(), publishedEvent.kitchenLocationId);
        assertEquals(List.of(A_UNIQUE_ID), publishedEvent.mealKitIds);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldGetKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, MANY_IDS);

        verify(kitchenRepository).get();
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldConfirmCooked() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, MANY_IDS);

        verify(kitchen).confirmCooked(A_COOK_ID, MANY_IDS);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldSaveOrUpdateKitchen() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, MANY_IDS);

        verify(kitchenRepository).saveOrUpdate(kitchen);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldPublishMealKitsCookedEvent() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);
        ArgumentCaptor<MealKitsCookedEvent> eventCaptor = ArgumentCaptor.forClass(MealKitsCookedEvent.class);

        cookingService.confirmCooked(A_COOK_ID, MANY_IDS);

        verify(eventBus).publish(eventCaptor.capture());
        MealKitsCookedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(A_KITCHEN_LOCATION_ID.value(), publishedEvent.kitchenLocationId);
        assertEquals(MANY_IDS, publishedEvent.mealKitIds);
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldSaveOrUpdate() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);

        cookingService.confirmCooked(A_COOK_ID, List.of(A_UNIQUE_ID, ANOTHER_UNIQUE_ID));

        verify(kitchenRepository).saveOrUpdate(kitchen);
    }
}
