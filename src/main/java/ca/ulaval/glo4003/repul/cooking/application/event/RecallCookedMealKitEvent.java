package ca.ulaval.glo4003.repul.cooking.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class RecallCookedMealKitEvent extends RepULEvent {
    public final UniqueIdentifier mealKitId;

    public RecallCookedMealKitEvent(UniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
    }
}
