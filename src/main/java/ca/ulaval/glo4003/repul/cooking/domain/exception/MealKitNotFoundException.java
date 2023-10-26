package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class MealKitNotFoundException extends CookingException {
    public MealKitNotFoundException(UniqueIdentifier mealKitId) {
        super("Meal kit with id " + mealKitId.value().toString() + " not found.");
    }
}
