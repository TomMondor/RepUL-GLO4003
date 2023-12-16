package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotForKitchenPickUpException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;

public class PreparedMealKits extends KitchenMealKits {
    public PreparedMealKits() {
        super();
    }

    public MealKit unconfirm(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = getMealKit(mealKitId);
        mealKits.remove(mealKitId);

        mealKit.unconfirmPreparation();
        return mealKit;
    }

    public MealKit pickUpNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        if (!mealKits.containsKey(mealKitId)) {
            throw new MealKitNotFoundException(mealKitId);
        }
        MealKit mealKit = mealKits.get(mealKitId);

        if (!mealKit.isForSubscriber(subscriberId)) {
            throw new MealKitNotFoundException(mealKitId);
        } else if (mealKit.isToBeDelivered()) {
            throw new MealKitNotForKitchenPickUpException(mealKitId);
        }

        mealKits.remove(mealKitId);

        return mealKit;
    }

    public void pickUpMealKitsForDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(id -> mealKits.remove(id));
    }
}
