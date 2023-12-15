package ca.ulaval.glo4003.repul.cooking.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.Cook.Cook;
import ca.ulaval.glo4003.repul.cooking.domain.Cook.Cooks;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.KitchenMealKits;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKitFactory;

public class Kitchen {
    private final KitchenLocationId kitchenLocationId;
    private final KitchenMealKits kitchenMealKits;
    private final MealKitFactory mealKitFactory;
    private final Cooks cooks;

    public Kitchen(MealKitFactory mealKitFactory) {
        this.mealKitFactory = mealKitFactory;
        this.kitchenMealKits = new KitchenMealKits();
        kitchenLocationId = KitchenLocationId.DESJARDINS;
        cooks = new Cooks();
    }

    public void hireCook(Cook cook) {
        cooks.add(cook);
    }

    public KitchenLocationId getKitchenLocationId() {
        return kitchenLocationId;
    }

    public void createMealKitInPreparation(
        MealKitUniqueIdentifier mealKitId,
        SubscriptionUniqueIdentifier subscriptionId,
        SubscriberUniqueIdentifier subscriberId,
        MealKitType type,
        LocalDate deliveryDate,
        Optional<DeliveryLocationId> deliveryLocationId
    ) {
        MealKit mealKit = mealKitFactory.createMealKit(mealKitId, subscriptionId, subscriberId, type, deliveryDate, deliveryLocationId);
        kitchenMealKits.add(mealKit);
    }

    public List<MealKit> getMealKitsToCook() {
        return kitchenMealKits.getMealKitsToCook();
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        Cook cook = cooks.get(cookId);

        List<MealKit> mealKitsToSelect = kitchenMealKits.removeMealKitsToSelect(selectedMealKitIds);

        cook.selectMealKits(mealKitsToSelect);
    }

    public List<MealKitUniqueIdentifier> getSelection(CookUniqueIdentifier cookId) {
        Cook cook = cooks.get(cookId);
        return cook.getSelectedMealKitsIds();
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Cook cook = cooks.get(cookId);

        MealKit unselectedMealKit = cook.unselectMealKit(mealKitId);

        kitchenMealKits.add(unselectedMealKit);
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        Cook cook = cooks.get(cookId);

        List<MealKit> unselectedMealKits = cook.unselectAllMealKits();

        kitchenMealKits.add(unselectedMealKits);
    }

    public MealKit confirmCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Cook cook = cooks.get(cookId);

        MealKit cookedMealKit = cook.confirmMealKitAssembled(mealKitId);

        kitchenMealKits.add(cookedMealKit);

        return cookedMealKit;
    }

    public List<MealKit> confirmCooked(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        Cook cook = cooks.get(cookId);

        List<MealKit> cookedMealKits = cook.confirmMealKitsCooked(mealKitIds);

        kitchenMealKits.add(cookedMealKits);

        return cookedMealKits;
    }

    public void recallCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = kitchenMealKits.recall(mealKitId);

        Cook cook = cooks.get(cookId);
        cook.selectMealKit(mealKit);
    }

    public void pickUpMealKitsForDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        kitchenMealKits.removeMealKits(mealKitIds);
    }

    public MealKit pickUpNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        return kitchenMealKits.pickUpNonDeliverableMealKit(subscriberId, mealKitId);
    }
}
