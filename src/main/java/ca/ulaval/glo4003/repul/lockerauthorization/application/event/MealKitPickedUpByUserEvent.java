package ca.ulaval.glo4003.repul.lockerauthorization.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;

public class MealKitPickedUpByUserEvent extends RepULEvent {
    public final MealKitDto mealKitDto;

    public MealKitPickedUpByUserEvent(MealKitDto mealKitDto) {
        super();
        this.mealKitDto = mealKitDto;
    }
}
