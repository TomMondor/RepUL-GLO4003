package ca.ulaval.glo4003.repul.cooking.application.event;

import ca.ulaval.glo4003.repul.commons.application.MealKitDto;

public record MealKitCookedDto(
    MealKitDto mealKitDto,
    boolean isToBeDelivered
) {
}
