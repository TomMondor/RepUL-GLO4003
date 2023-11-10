package ca.ulaval.glo4003.repul.lockerauthorization.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class MealKitPickedUpByUserEvent extends RepULEvent {
    public final UniqueIdentifier mealKitId;

    public MealKitPickedUpByUserEvent(UniqueIdentifier mealKitId) {
        super();
        this.mealKitId = mealKitId;
    }
}
