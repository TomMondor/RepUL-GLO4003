package ca.ulaval.glo4003.repul.subscription.api;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.identitymanagement.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserInKitchenEvent;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;

import com.google.common.eventbus.Subscribe;

public class SubscriberEventHandler {
    private final SubscriberService subscriberService;

    public SubscriberEventHandler(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Subscribe
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(userCreatedEvent.userId);
        subscriberService.createSubscriber(subscriberId, userCreatedEvent.idul, userCreatedEvent.name, userCreatedEvent.birthdate,
            userCreatedEvent.gender, userCreatedEvent.email);
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        subscriberService.updateOrdersToInDelivery(event.mealKitDtos);
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent event) {
        subscriberService.updateOrderToReadyToPickUp(event.mealKitDto);
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent event) {
        subscriberService.updateOrderToInPreparation(event.mealKitDto);
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(MealKitPickedUpByUserEvent event) {
        subscriberService.updateOrderToPickedUp(event.mealKitDto);
    }

    @Subscribe
    public void handleMealKitPickedUpByUserInKitchenEvent(MealKitPickedUpByUserInKitchenEvent event) {
        subscriberService.updateOrderToPickedUp(event.mealKitDto);
    }

    @Subscribe
    public void handleMealKitCookedEvent(MealKitsCookedEvent event) {
        event.mealKits.forEach(mealKitCookedDto -> {
            if (!mealKitCookedDto.isToBeDelivered()) {
                subscriberService.updateOrderToReadyToPickUp(mealKitCookedDto.mealKitDto());
            }
        });
    }
}
