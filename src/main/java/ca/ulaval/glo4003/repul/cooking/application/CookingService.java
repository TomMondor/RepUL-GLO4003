package ca.ulaval.glo4003.repul.cooking.application;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;
import ca.ulaval.glo4003.repul.cooking.domain.exception.KitchenNotFoundException;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import com.google.common.eventbus.Subscribe;

public class CookingService {
    private final KitchenRepository kitchenRepository;

    private final RepULEventBus eventBus;

    public CookingService(KitchenRepository kitchenRepository, RepULEventBus eventBus) {
        this.kitchenRepository = kitchenRepository;
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent event) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.addMealKit(event.mealKitId, event.mealKitType, event.deliveryDate);

        kitchenRepository.save(kitchen);
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.removeMealKitsFromKitchen(event.mealKitIds);

        kitchenRepository.save(kitchen);
    }

    public MealKitsPayload getMealKitsToCook() {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        return new MealKitsPayload(
            kitchen.getMealKitsToCook().stream().map(mealKit -> new MealKitPayload(mealKit.getMealKitId(), mealKit.getDeliveryDate(), mealKit.getRecipes()))
                .toList());
    }

    public void select(UniqueIdentifier cookId, List<UniqueIdentifier> selectedMealKitIds) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.select(cookId, selectedMealKitIds);

        kitchenRepository.save(kitchen);
    }

    public List<UniqueIdentifier> getSelection(UniqueIdentifier cookId) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        return kitchen.getSelection(cookId);
    }

    public void cancelOneSelected(UniqueIdentifier cookId, UniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.cancelOneSelected(cookId, mealKitId);

        kitchenRepository.save(kitchen);
    }

    public void cancelAllSelected(UniqueIdentifier cookId) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.cancelAllSelected(cookId);

        kitchenRepository.save(kitchen);
    }

    public void confirmCooked(UniqueIdentifier cookId, UniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.confirmCooked(cookId, mealKitId);

        kitchenRepository.save(kitchen);

        eventBus.publish(new MealKitsCookedEvent(kitchen.getKitchenLocationId().value(), List.of(mealKitId)));
    }

    public void confirmCooked(UniqueIdentifier cookId, List<UniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.confirmCooked(cookId, mealKitIds);

        kitchenRepository.save(kitchen);

        eventBus.publish(new MealKitsCookedEvent(kitchen.getKitchenLocationId().value(), mealKitIds));
    }

    public void recallCooked(UniqueIdentifier cookId, UniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenRepository.get().orElseThrow(KitchenNotFoundException::new);

        kitchen.recallCooked(cookId, mealKitId);

        kitchenRepository.save(kitchen);

        eventBus.publish(new RecallCookedMealKitEvent(mealKitId));
    }
}
