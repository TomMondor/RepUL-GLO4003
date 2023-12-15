package ca.ulaval.glo4003.repul.cooking.domain.mealkit;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;

public class MealKit {
    private final MealKitUniqueIdentifier mealKitId;
    private final SubscriptionUniqueIdentifier subscriptionId;
    private final SubscriberUniqueIdentifier subscriberId;
    private final LocalDate dateOfReceipt;
    private final List<Recipe> recipes;
    private final boolean isToBeDelivered;
    private boolean isCooked;

    public MealKit(MealKitUniqueIdentifier mealKitId, SubscriptionUniqueIdentifier subscriptionId, SubscriberUniqueIdentifier subscriberId,
                   LocalDate dateOfReceipt, List<Recipe> recipes, boolean isToBeDelivered) {
        this.mealKitId = mealKitId;
        this.subscriptionId = subscriptionId;
        this.subscriberId = subscriberId;
        this.dateOfReceipt = dateOfReceipt;
        this.recipes = recipes;
        this.isCooked = false;
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

    public boolean isForSubscriber(SubscriberUniqueIdentifier subscriberId) {
        return this.subscriberId.equals(subscriberId);
    }

    public boolean isDeliveryTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return dateOfReceipt.isEqual(tomorrow);
    }

    public boolean isToCookToday() {
        return !isCooked() && isDeliveryTomorrow();
    }

    public void setCooked() {
        this.isCooked = true;
    }

    public void recall() {
        if (!isCooked()) {
            throw new MealKitNotCookedException();
        }
        this.isCooked = false;
    }

    public boolean isCooked() {
        return isCooked;
    }

    public boolean isToBeDelivered() {
        return isToBeDelivered;
    }

    public MealKitDto toDto() {
        return new MealKitDto(subscriberId, subscriptionId, mealKitId);
    }
}
