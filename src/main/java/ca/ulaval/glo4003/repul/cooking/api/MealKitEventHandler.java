package ca.ulaval.glo4003.repul.cooking.api;

import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import com.google.common.eventbus.Subscribe;

public class MealKitEventHandler {
    private final CookingService cookingService;

    public MealKitEventHandler(CookingService cookingService) {
        this.cookingService = cookingService;
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent event) {
        cookingService.createMealKitInPreparation(
            event.mealKitId,
            event.subscriptionId,
            event.subscriberId,
            event.mealKitType,
            event.deliveryDate,
            event.deliveryLocationId
        );
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        cookingService.giveMealKitsToDelivery(event.mealKitDtos.stream().map(MealKitDto::mealKitId).toList());
    }
}
