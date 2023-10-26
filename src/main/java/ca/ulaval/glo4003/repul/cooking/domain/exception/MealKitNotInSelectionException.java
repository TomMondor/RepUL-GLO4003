package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class MealKitNotInSelectionException extends CookingException {
    public MealKitNotInSelectionException(UniqueIdentifier mealKitId) {
        super("The meal kit with id " + mealKitId.value().toString() + " is not in your selection.");
    }
}
