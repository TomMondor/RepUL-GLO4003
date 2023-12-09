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
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;

public class CookingService {
    private final KitchenPersister kitchenPersister;

    private final RepULEventBus eventBus;

    public CookingService(KitchenPersister kitchenPersister, RepULEventBus eventBus) {
        this.kitchenPersister = kitchenPersister;
        this.eventBus = eventBus;
    }

    public void receiveMealKitInKitchen(MealKitUniqueIdentifier mealKitId, MealKitType mealKitType, LocalDate deliveryDate) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.addMealKit(mealKitId, mealKitType, deliveryDate);

        kitchenPersister.save(kitchen);
    }

    public void giveMealKitToDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.removeMealKitsFromKitchen(mealKitIds);

        kitchenPersister.save(kitchen);
    }

    public MealKitsPayload getMealKitsToCook() {
        Kitchen kitchen = kitchenPersister.get();

        List<MealKitPayload> mealKitPayloads = kitchen.getMealKitsToCook().stream().map(
            mealKit -> new MealKitPayload(mealKit.getMealKitId(), mealKit.getDeliveryDate(), mealKit.getRecipes())
        ).toList();
        return new MealKitsPayload(mealKitPayloads);
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.select(cookId, selectedMealKitIds);

        kitchenPersister.save(kitchen);
    }

    public List<MealKitUniqueIdentifier> getSelection(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenPersister.get();

        return kitchen.getSelection(cookId);
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.cancelOneSelected(cookId, mealKitId);

        kitchenPersister.save(kitchen);
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.cancelAllSelected(cookId);

        kitchenPersister.save(kitchen);
    }

    public void confirmCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.confirmCooked(cookId, mealKitId);

        kitchenPersister.save(kitchen);

        String kitchenLocationId = kitchen.getKitchenLocationId().toString();
        eventBus.publish(new MealKitsCookedEvent(kitchenLocationId, List.of(mealKitId)));
    }

    public void confirmCooked(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.confirmCooked(cookId, mealKitIds);

        kitchenPersister.save(kitchen);

        String kitchenLocationId = kitchen.getKitchenLocationId().toString();
        eventBus.publish(new MealKitsCookedEvent(kitchenLocationId, mealKitIds));
    }

    public void recallCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.recallCooked(cookId, mealKitId);

        kitchenPersister.save(kitchen);

        eventBus.publish(new RecallCookedMealKitEvent(mealKitId));
    }
}
