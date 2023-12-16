package ca.ulaval.glo4003.repul.cooking.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class UnconfirmPreparationMealKitEvent extends RepULEvent {
    public final MealKitUniqueIdentifier mealKitId;

    public UnconfirmPreparationMealKitEvent(MealKitUniqueIdentifier mealKitId) {
        super();
        this.mealKitId = mealKitId;
    }
}
