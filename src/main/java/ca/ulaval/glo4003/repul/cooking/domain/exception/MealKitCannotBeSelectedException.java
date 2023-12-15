package ca.ulaval.glo4003.repul.cooking.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class MealKitCannotBeSelectedException extends CookingException {
    public MealKitCannotBeSelectedException(MealKitUniqueIdentifier mealKitId) {
        super(String.format("The meal kit id %s cannot be selected.", mealKitId.getUUID().toString()));
    }
}
