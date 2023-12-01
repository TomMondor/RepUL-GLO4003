package ca.ulaval.glo4003.repul.cooking.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;

public class MealKit {
    private final MealKitUniqueIdentifier mealKitId;
    private final List<Recipe> recipes;
    private final LocalDate deliveryDate;
    private Optional<CookUniqueIdentifier> cookId = Optional.empty();
    private boolean isCooked;

    public MealKit(MealKitUniqueIdentifier mealKitId, LocalDate deliveryDate, List<Recipe> recipes) {
        this.mealKitId = mealKitId;
        this.recipes = recipes;
        this.deliveryDate = deliveryDate;
        this.isCooked = false;
    }

    public MealKitUniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void selectBy(CookUniqueIdentifier cookId) {
        this.cookId = Optional.of(cookId);
    }

    public void unselect() {
        cookId = Optional.empty();
    }

    public boolean isSelectedBy(CookUniqueIdentifier cookId) {
        return this.cookId.equals(Optional.of(cookId)) && !isCooked;
    }

    public boolean isUnselected() {
        return !cookId.isPresent();
    }

    public boolean isDeliveryTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return deliveryDate.isEqual(tomorrow);
    }

    public void setCooked() {
        this.isCooked = true;
    }

    public void recallMealKit(CookUniqueIdentifier cookId) {
        if (!isCooked()) {
            throw new MealKitNotCookedException();
        }
        this.isCooked = false;
        this.cookId = Optional.of(cookId);
    }

    public boolean isCooked() {
        return isCooked;
    }
}
