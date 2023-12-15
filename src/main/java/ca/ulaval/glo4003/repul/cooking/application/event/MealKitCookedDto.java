package ca.ulaval.glo4003.repul.cooking.application.event;

import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;

public record MealKitCookedDto(
    MealKitDto mealKitDto,
    boolean isToBeDelivered
) {
}
