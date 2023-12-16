package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotPreparedException;
import ca.ulaval.glo4003.repul.cooking.domain.recipe.Recipe;

public class MealKit {
    private final MealKitUniqueIdentifier mealKitId;
    private final SubscriptionUniqueIdentifier subscriptionId;
    private final SubscriberUniqueIdentifier subscriberId;
    private final LocalDate dateOfReceipt;
    private final List<Recipe> recipes;
    private final boolean isToBeDelivered;
    private boolean isPrepared;

    public MealKit(MealKitUniqueIdentifier mealKitId, SubscriptionUniqueIdentifier subscriptionId, SubscriberUniqueIdentifier subscriberId,
                   LocalDate dateOfReceipt, List<Recipe> recipes, boolean isToBeDelivered) {
        this.mealKitId = mealKitId;
        this.subscriptionId = subscriptionId;
        this.subscriberId = subscriberId;
        this.dateOfReceipt = dateOfReceipt;
        this.recipes = recipes;
        this.isPrepared = false;
        this.isToBeDelivered = isToBeDelivered;
    }

    public MealKitUniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public LocalDate getDateOfReceipt() {
        return dateOfReceipt;
    }

    public SubscriptionUniqueIdentifier getSubscriptionId() {
        return subscriptionId;
    }

    public boolean isForSubscriber(SubscriberUniqueIdentifier subscriberId) {
        return this.subscriberId.equals(subscriberId);
    }

    public boolean isDeliveryTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return dateOfReceipt.isEqual(tomorrow);
    }

    public boolean isToPrepareToday() {
        return !isPrepared() && isDeliveryTomorrow();
    }

    public void confirmPreparation() {
        this.isPrepared = true;
    }

    public void unconfirmPreparation() {
        if (!isPrepared()) {
            throw new MealKitNotPreparedException();
        }
        this.isPrepared = false;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isToBeDelivered() {
        return isToBeDelivered;
    }

    public MealKitDto toDto() {
        return new MealKitDto(subscriberId, subscriptionId, mealKitId);
    }
}
