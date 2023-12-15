package ca.ulaval.glo4003.repul.cooking.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;

public class MealKitsCookedEvent extends RepULEvent {
    public final String kitchenLocationId;
    public final List<MealKitCookedDto> mealKits;

    public MealKitsCookedEvent(String kitchenLocationId, List<MealKitCookedDto> mealKits) {
        super();
        this.kitchenLocationId = kitchenLocationId;
        this.mealKits = mealKits;
    }
}
