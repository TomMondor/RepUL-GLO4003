package ca.ulaval.glo4003.repul.cooking.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class MealKitsCookedEvent extends RepULEvent {
    public final String kitchenLocationId;
    public final List<UniqueIdentifier> mealKitIds;

    public MealKitsCookedEvent(String kitchenLocationId, List<UniqueIdentifier> mealKitIds) {
        this.kitchenLocationId = kitchenLocationId;
        this.mealKitIds = mealKitIds;
    }
}
