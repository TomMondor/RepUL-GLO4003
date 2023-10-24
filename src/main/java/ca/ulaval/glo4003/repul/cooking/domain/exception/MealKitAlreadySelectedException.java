package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class MealKitAlreadySelectedException extends CookingException {
    public MealKitAlreadySelectedException(UniqueIdentifier mealKitId) {
        super("The meal kit with id " + mealKitId.toString() + " is not available to cook.");
    }
}
