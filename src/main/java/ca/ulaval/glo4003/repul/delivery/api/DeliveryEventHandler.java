package ca.ulaval.glo4003.repul.delivery.api;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class DeliveryEventHandler {
    private DeliveryService deliveryService;

    public DeliveryEventHandler(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Subscribe
    public void handleDeliveryPersonAccountCreatedEvent(
        DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent) {
        deliveryService.createDeliveryPersonAccount(deliveryPersonAccountCreatedEvent.accountId);
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent mealKitsCookedEvent) {
        deliveryService.createCargo(new KitchenLocationId(mealKitsCookedEvent.kitchenLocationId),
            mealKitsCookedEvent.mealKitIds);
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        deliveryService.receiveMealKitForDelivery(mealKitConfirmedEvent.deliveryLocationId,
            mealKitConfirmedEvent.mealKitId);
    }

    @Subscribe
    public void handleRecallCookedMealKitEvent(RecallCookedMealKitEvent recallCookedMealKitEvent) {
        deliveryService.recallMealKit(recallCookedMealKitEvent.mealKitId);
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(
        MealKitPickedUpByUserEvent mealKitPickedUpByUserEvent) {
        deliveryService.removeMealKitFromLocker(mealKitPickedUpByUserEvent.mealKitId);
    }
}
