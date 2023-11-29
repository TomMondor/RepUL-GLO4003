package ca.ulaval.glo4003.repul.cooking.api;

import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import com.google.common.eventbus.Subscribe;

public class MealKitEventHandler {
    private CookingService cookingService;

    public MealKitEventHandler(CookingService cookingService) {
        this.cookingService = cookingService;
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent event) {
        cookingService.receiveMealKitInKitchen(event.mealKitId, event.mealKitType, event.deliveryDate);
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        cookingService.giveMealKitToDelivery(event.mealKitIds);
    }
}
