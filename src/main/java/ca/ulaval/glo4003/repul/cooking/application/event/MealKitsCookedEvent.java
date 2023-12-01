package ca.ulaval.glo4003.repul.cooking.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;

public class MealKitsCookedEvent extends RepULEvent {
    public final String kitchenLocationId;
    public final List<MealKitUniqueIdentifier> mealKitIds;

    public MealKitsCookedEvent(String kitchenLocationId, List<MealKitUniqueIdentifier> mealKitIds) {
        super();
        this.kitchenLocationId = kitchenLocationId;
        this.mealKitIds = mealKitIds;
    }
}
