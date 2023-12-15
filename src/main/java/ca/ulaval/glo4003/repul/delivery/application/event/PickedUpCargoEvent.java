package ca.ulaval.glo4003.repul.delivery.application.event;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;

public class PickedUpCargoEvent extends RepULEvent {
    public final List<MealKitDto> mealKitDtos;

    public PickedUpCargoEvent(List<MealKitDto> mealKitDtos) {
        super();
        this.mealKitDtos = mealKitDtos;
    }
}
