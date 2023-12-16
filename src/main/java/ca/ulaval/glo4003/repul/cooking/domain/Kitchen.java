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
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKitFactory;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.PreparedMealKits;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.ToPrepareMealKits;

public class Kitchen {
    private final KitchenLocationId kitchenLocationId;
    private final ToPrepareMealKits toPrepareMealKits;
    private final PreparedMealKits preparedMealKits;
    private final MealKitFactory mealKitFactory;
    private final Cooks cooks;

    public Kitchen(MealKitFactory mealKitFactory) {
        this.mealKitFactory = mealKitFactory;
        this.toPrepareMealKits = new ToPrepareMealKits();
        this.preparedMealKits = new PreparedMealKits();
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
        toPrepareMealKits.add(mealKit);
    }

    public List<MealKit> getMealKitsToPrepare() {
        return toPrepareMealKits.getMealKitsToPrepare();
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        Cook cook = cooks.get(cookId);

        List<MealKit> mealKitsToSelect = toPrepareMealKits.removeMealKitsToSelect(selectedMealKitIds);

        cook.selectMealKits(mealKitsToSelect);
    }

    public List<MealKitUniqueIdentifier> getSelection(CookUniqueIdentifier cookId) {
        Cook cook = cooks.get(cookId);
        return cook.getSelectedMealKitsIds();
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Cook cook = cooks.get(cookId);

        MealKit unselectedMealKit = cook.unselectMealKit(mealKitId);

        toPrepareMealKits.add(unselectedMealKit);
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        Cook cook = cooks.get(cookId);

        List<MealKit> unselectedMealKits = cook.unselectAllMealKits();

        toPrepareMealKits.add(unselectedMealKits);
    }

    public MealKit confirmPreparation(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Cook cook = cooks.get(cookId);

        MealKit preparedMealKit = cook.confirmMealKitPrepared(mealKitId);

        preparedMealKits.add(preparedMealKit);

        return preparedMealKit;
    }

    public List<MealKit> confirmPreparation(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        Cook cook = cooks.get(cookId);

        List<MealKit> newCookedMealKits = cook.confirmMealKitsPrepared(mealKitIds);

        preparedMealKits.add(newCookedMealKits);

        return newCookedMealKits;
    }

    public void unconfirmPreparation(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = preparedMealKits.unconfirm(mealKitId);

        Cook cook = cooks.get(cookId);
        cook.selectMealKit(mealKit);
    }

    public void pickUpMealKitsForDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        preparedMealKits.pickUpMealKitsForDelivery(mealKitIds);
    }

    public MealKit pickUpNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        return preparedMealKits.pickUpNonDeliverableMealKit(subscriberId, mealKitId);
    }
}
