package ca.ulaval.glo4003.repul.cooking.application;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;

public class CookingService {
    private final KitchenRepository kitchenRepository;

    private final RepULEventBus eventBus;

    public CookingService(KitchenRepository kitchenRepository, RepULEventBus eventBus) {
        this.kitchenRepository = kitchenRepository;
        this.eventBus = eventBus;
    }

    public void receiveMealKitInKitchen(MealKitUniqueIdentifier mealKitId, MealKitType mealKitType, LocalDate deliveryDate) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.addMealKit(mealKitId, mealKitType, deliveryDate);

        kitchenRepository.save(kitchen);
    }

    public void giveMealKitToDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.removeMealKitsFromKitchen(mealKitIds);

        kitchenRepository.save(kitchen);
    }

    public MealKitsPayload getMealKitsToCook() {
        Kitchen kitchen = kitchenRepository.get();

        return new MealKitsPayload(
            kitchen.getMealKitsToCook().stream().map(mealKit -> new MealKitPayload(mealKit.getMealKitId(), mealKit.getDeliveryDate(), mealKit.getRecipes()))
                .toList());
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.select(cookId, selectedMealKitIds);

        kitchenRepository.save(kitchen);
    }

    public List<MealKitUniqueIdentifier> getSelection(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenRepository.get();

        return kitchen.getSelection(cookId);
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.cancelOneSelected(cookId, mealKitId);

        kitchenRepository.save(kitchen);
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.cancelAllSelected(cookId);

        kitchenRepository.save(kitchen);
    }

    public void confirmCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.confirmCooked(cookId, mealKitId);

        kitchenRepository.save(kitchen);

        eventBus.publish(new MealKitsCookedEvent(kitchen.getKitchenLocationId().value(), List.of(mealKitId)));
    }

    public void confirmCooked(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.confirmCooked(cookId, mealKitIds);

        kitchenRepository.save(kitchen);

        eventBus.publish(new MealKitsCookedEvent(kitchen.getKitchenLocationId().value(), mealKitIds));
    }

    public void recallCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenRepository.get();

        kitchen.recallCooked(cookId, mealKitId);

        kitchenRepository.save(kitchen);

        eventBus.publish(new RecallCookedMealKitEvent(mealKitId));
    }
}
