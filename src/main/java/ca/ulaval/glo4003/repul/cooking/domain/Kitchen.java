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
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitAlreadySelectedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotForKitchenPickUpException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotInSelectionException;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKitFactory;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKits;

public class Kitchen {
    private final KitchenLocationId kitchenLocationId;
    private final MealKits mealKits;
    private final MealKitFactory mealKitFactory;

    public Kitchen(MealKitFactory mealKitFactory) {
        this.mealKitFactory = mealKitFactory;
        this.mealKits = new MealKits();
        kitchenLocationId = KitchenLocationId.DESJARDINS;
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
        mealKits.add(mealKit);
    }

    public List<MealKit> getMealKitsToCook() {
        return mealKits.getMealKitsToCook();
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        verifyMealKitsAreUnassigned(selectedMealKitIds);

        mealKits.selectMealKitsByCook(cookId, selectedMealKitIds);
    }

    public List<MealKitUniqueIdentifier> getSelection(CookUniqueIdentifier cookId) {
        return mealKits.getMealKitsIdSelectedByCook(cookId);
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        verifyMealKitsInCookSelection(cookId, List.of(mealKitId));

        mealKits.unselectMealKits(List.of(mealKitId));
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        mealKits.unselectMealKitsByCook(cookId);
    }

    public MealKit confirmCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        verifyMealKitsInCookSelection(cookId, List.of(mealKitId));

        return mealKits.confirmMealKitCooked(mealKitId);
    }

    public List<MealKit> confirmCooked(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        verifyMealKitsInCookSelection(cookId, mealKitIds);

        return mealKits.confirmMealKitsCooked(mealKitIds);
    }

    public void recallCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        mealKits.recallMealKit(mealKitId, cookId);
    }

    public void removeMealKitsFromKitchen(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKits.removeMealKits(mealKitIds);
    }

    public MealKit pickupNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = mealKits.getMealKit(mealKitId);

        if (!mealKit.isForSubscriber(subscriberId)) {
            throw new MealKitNotFoundException(mealKitId);
        } else if (!mealKit.isCooked()) {
            throw new MealKitNotCookedException();
        } else if (mealKit.isToBeDelivered()) {
            throw new MealKitNotForKitchenPickUpException(mealKitId);
        }

        mealKits.remove(mealKitId);
        return mealKit;
    }

    private void verifyMealKitsAreUnassigned(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(this::verifyMealKitIsUnassigned);
    }

    private void verifyMealKitIsUnassigned(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = mealKits.getMealKit(mealKitId);
        if (!mealKit.isUnselected()) {
            throw new MealKitAlreadySelectedException(mealKitId);
        }
    }

    private void verifyMealKitsInCookSelection(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        List<MealKitUniqueIdentifier> cookSelection = getSelection(cookId);
        mealKitIds.forEach(mealKitId -> {
            if (!cookSelection.contains(mealKitId)) {
                throw new MealKitNotInSelectionException(mealKitId);
            }
        });
    }
}
