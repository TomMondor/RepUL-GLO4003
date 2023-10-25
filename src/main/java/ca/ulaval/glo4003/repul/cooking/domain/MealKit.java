package ca.ulaval.glo4003.repul.cooking.domain;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;

public class MealKit {
    private final UniqueIdentifier mealKitId;
    private final List<Recipe> recipes;
    private final LocalDate deliveryDate;
    private UniqueIdentifier cookId;
    private boolean isCooked;

    public MealKit(UniqueIdentifier mealKitId, LocalDate deliveryDate, List<Recipe> recipes) {
        this.mealKitId = mealKitId;
        this.recipes = recipes;
        this.deliveryDate = deliveryDate;
        this.isCooked = false;
    }

    public void setCookId(UniqueIdentifier cookId) {
        this.cookId = cookId;
    }

    public UniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void unselect() {
        cookId = null;
    }

    public boolean isSelectedBy(UniqueIdentifier cookId) {
        return this.cookId == cookId && !isCooked;
    }

    public boolean isUnselected() {
        return cookId == null;
    }

    public boolean isDeliveryTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return deliveryDate.isEqual(tomorrow);
    }

    public void setCooked() {
        this.isCooked = true;
    }

    public void recallMealKit(UniqueIdentifier cookId) {
        if (!isCooked()) {
            throw new MealKitNotCookedException();
        }
        this.isCooked = false;
        this.cookId = cookId;
    }

    public boolean isCooked() {
        return isCooked;
    }
}
