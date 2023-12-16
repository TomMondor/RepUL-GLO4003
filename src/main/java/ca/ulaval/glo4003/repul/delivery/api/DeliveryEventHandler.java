package ca.ulaval.glo4003.repul.delivery.api;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitCookedDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.UnconfirmPreparationMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.identitymanagement.application.event.DeliveryPersonAccountCreatedEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import com.google.common.eventbus.Subscribe;

public class DeliveryEventHandler {
    private final DeliveryService deliveryService;

    public DeliveryEventHandler(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Subscribe
    public void handleDeliveryPersonAccountCreatedEvent(DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent) {
        deliveryService.createDeliveryPersonAccount(deliveryPersonAccountCreatedEvent.accountId);
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        mealKitConfirmedEvent.deliveryLocationId.ifPresent(deliveryLocationId ->
            deliveryService.createPendingMealKit(mealKitConfirmedEvent.subscriberId, mealKitConfirmedEvent.subscriptionId,
                mealKitConfirmedEvent.mealKitId, deliveryLocationId
            ));
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent mealKitsCookedEvent) {
        KitchenLocationId kitchenLocationId = KitchenLocationId.valueOf(mealKitsCookedEvent.kitchenLocationId);

        List<MealKitUniqueIdentifier> mealKitsToDeliver = mealKitsCookedEvent.mealKits.stream()
            .filter(MealKitCookedDto::isToBeDelivered).map(mealKitCookedDto -> mealKitCookedDto.mealKitDto().mealKitId()).toList();

        deliveryService.createCargo(kitchenLocationId, mealKitsToDeliver);
    }

    @Subscribe
    public void handleUnconfirmPreparationMealKitEvent(UnconfirmPreparationMealKitEvent unconfirmPreparationMealKitEvent) {
        deliveryService.recallMealKitToPending(unconfirmPreparationMealKitEvent.mealKitId);
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(
        MealKitPickedUpByUserEvent mealKitPickedUpByUserEvent) {
        deliveryService.removeMealKitFromLocker(mealKitPickedUpByUserEvent.mealKitDto.mealKitId());
    }
}
